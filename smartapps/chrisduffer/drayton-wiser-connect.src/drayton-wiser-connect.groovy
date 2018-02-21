/**
 *  Drayton Wiser (Connect)
 *
 *  Copyright 2017 Chris Evans
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *  for the specific language governing permissions and limitations under the License.
 *
 */
 
 import groovy.json.JsonSlurper
 
definition(
    name: "Drayton Wiser (Connect)",
    namespace: "chrisduffer",
    author: "Chris Evans",
    description: "Drayton Wiser (Connect)",
    category: "",
    iconUrl: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience.png",
    iconX2Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png",
    iconX3Url: "https://s3.amazonaws.com/smartapp-icons/Convenience/Cat-Convenience@2x.png") {
}


preferences {
    page(name:"firstPage", title:"Drayton Wiser Device Setup", content:"firstPage", install: true)
}

def firstPage() {
	return dynamicPage(name: "firstPage", title: "", install: true, uninstall: true) {
        section("Title") {
            input("ipAddress", "string", title: "Hub IP address", description: "Your Drayton Wiser IP Address e.g. (192.168.1.224)", required: true)
            input("secret", "string", title: "Hub Secret", required: true)
            input("maxRooms", "number", title: "Rooms to add (e.g. 4)", required: true)
            input("boostMins", "number", title: "Boost minutes (e.g. 30)", required: true)
            input("awayTemp", "number", title: "Away Temperature (e.g. 10)", required: true)
        }
    }
}

Map apiRequestHeaders() {
	log.debug("headers ${secret}  HOST: ${ipAddress}")
	return ["User-Agent": "SmartThings Integration",
            "Secret": "${secret}",
            "HOST": "${ipAddress}"
	]
}

def apiGET(path) {
	try {
        def url = "${path}"
        log.debug("apiGET url ${url}" )


        def httpRequest = [
            method: "GET",
            path: "${path}",
            headers: apiRequestHeaders()
        ] 
        
        return httpRequest

    	//return new physicalgraph.device.HubAction(httpRequest)
    }
    catch (Exception e) {
    	log.debug("apiGET error ${e}" )
    }
}


private apiPatch(path, data) {
    try {
        def url = "${path}"
        log.debug "apiPatch (${path}, ${data})"

        def httpRequest = [
            method: "PATCH",
            path: "${path}",
            headers: apiRequestHeaders(),
            body: data
        ] 
        
        return httpRequest

    	//return new physicalgraph.device.HubAction(httpRequest)
    }
    catch (Exception e) {
    	log.debug("apiPatch error ${e}" )
    }
}


def installed() {
	log.debug "Installed with settings: ${settings}"

	initialize()
}

def updated() {
	log.debug "Updated with settings: ${settings}"

	unsubscribe()
	initialize()
    
    subscribe(location, null, response, [filterEvents:false]) 
}

def addRooms() {
	 if (maxRooms > 0){
    	log.debug "Add rooms"
        (1..maxRooms).each {
            def device = "${app.id}:${it}"
            def childDevice = getChildDevice("${device}")
            
            if (!childDevice){
            	 log.debug "Adding room ${device}"
                 
                  childDevice = addChildDevice(app.namespace, "Drayton Wiser Room", "$device", null, [
                	"label": "Drayton Room ${it}",
                    "pollingInterval": 1,
            ])
                log.debug "Added room ${device}"
            }
            else{
            	log.debug "room ${device} aleady added"
            }
            
        }
    }
}

def addHomeAway() {
	log.debug "Add away mode switch"
    def device = "${app.id}:AA"
    def childDevice = getChildDevice("${device}")
    
    if (!childDevice){
        log.debug "Adding away mode ${device}"

        childDevice = addChildDevice(app.namespace, "Drayton Wiser Away", "$device", null, [
            "label": "Drayton Away Mode",
            "pollingInterval": 1,
        ])
        log.debug "Added away mode ${device}"
    }
}

def initialize() {
	log.debug "initialize ${maxRooms}"
    
    addRooms()
     
    addHomeAway()
     
   
	// TODO: subscribe to attributes, devices, locations, etc.
}

def logResponse(response) {
	log.info("Status: ${response.status}")
	//log.info("Body: ${response.data}")
}

def getHeaders() {
	def headers = [:] 
    headers.put("HOST", "${ipAddress}:80")
    headers.put("Secret", "${secret}")
    
    return headers
}

def systemRefresh() {
	log.info("systemRefresh")
    
    def refreshRequest = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/data/domain/System/",
        headers: getHeaders()
        )

	log.debug " ${refreshRequest}"
	log.debug "sending systemRefresh"
    sendHubCommand(refreshRequest)
}

def childPollingTask(deviceId){
	log.info("childPollingTask ${deviceId}")
    childRefresh(deviceId)
}

def childRefresh(deviceId){
	log.info("childRefresh ${deviceId}")
    def childDevice = getChildDevice(deviceId)
    def idString = ("${deviceId}").split(':')
    //log.info("${idString}")
    log.info("idString ${idString[0]}  ${idString[1]}")
    
    def refreshRequest = new physicalgraph.device.HubAction(
        method: "GET",
        path: "/data/domain/Room/${idString[1]}",
        headers: getHeaders()
        )

	log.debug " ${refreshRequest}"
	log.debug "sending childRefresh"
    sendHubCommand(refreshRequest)
}


def childCancelOverride(deviceId){
	log.info("childCancelOverride ${deviceId}")
    
    def data = [RequestOverride:[Type:"None",Originator:"App",DurationMinutes:0,SetPoint:0]]
    
    def childDevice = getChildDevice(deviceId)
    def idString = ("${deviceId}").split(':')
    //log.info("${idString}")
    log.info("idString ${idString[0]}  ${idString[1]}")
    
    def cancelRequest = new physicalgraph.device.HubAction(
        method: "PATCH",
        path: "/data/domain/Room/${idString[1]}",
        headers: getHeaders(),
        body: data
        )

	log.debug "sending childCancelOverride"
    
    log.debug " ${cancelRequest}"
    sendHubCommand(cancelRequest) 
}

def childSetOverride(deviceId, temp){
	log.info("childSetOverride ${deviceId}  ${temp}")
    
    def data = [RequestOverride:[Type:"Manual",SetPoint:temp]]
    
    def childDevice = getChildDevice(deviceId)
    def idString = ("${deviceId}").split(':')
    //log.info("${idString}")
    log.info("idString ${idString[0]}  ${idString[1]}")
    
    def overrideRequest = new physicalgraph.device.HubAction(
        method: "PATCH",
        path: "/data/domain/Room/${idString[1]}",
        headers: getHeaders(),
        body: data
        )

	log.debug "sending childSetOverride cmd"
    
    log.debug " ${overrideRequest}"
    sendHubCommand(overrideRequest) 
}

void setAway() {
	def temp = awayTemp * 10
    log.info("setAway ${temp}")
    
    log.info("cancel room overrides")
    (1..maxRooms).each {
     	log.info("setAway room ${it}")
        def device = "${app.id}:${it}"
        childCancelOverride(device)
    }
    
    def data = [type:2,setPoint:temp]
    
    def awayRequest = new physicalgraph.device.HubAction(
        method: "PATCH",
        path: "/data/domain/System/RequestOverride",
        headers: getHeaders(),
        body: data
        )

	log.debug "sending setAway cmd"
    
    log.debug " ${awayRequest}"
    sendHubCommand(awayRequest)
}

void setHome() {
    
    log.info("setHome")
    
    def data = [type:0,setPoint:0]
    
    def homeRequest = new physicalgraph.device.HubAction(
        method: "PATCH",
        path: "/data/domain/System/RequestOverride",
        headers: getHeaders(),
        body: data
        )

	log.debug "sending setHome cmd"
    
    log.debug " ${homeRequest}"
    sendHubCommand(homeRequest)
}

void calledBackHandler(physicalgraph.device.HubResponse hubResponse) {
    log.debug "Entered calledBackHandler()..."
}


def parse(String message) {
	 log.debug "parse"

}


def locationHandler(evt) {
	 log.debug "locationHandler"
    def description = evt.description
    def hub = evt?.hubId
    
    log.debug "desc ${description}"
    
    //def bodyString = new String(description.split(',')[5].split(":")[1].decodeBase64())
    
    
    //def slurper = new JsonSlurper()
	
    /*
    log.debug "cp desc: " + description
    if (description.count(",") > 4)
    {
    def bodyString = new String(description.split(',')[5].split(":")[1].decodeBase64())
    log.debug(bodyString)
	}
    */
}

private String createDNI(ipaddr, port) {
    log.debug "createDNI(${ipaddr}, ${port})"

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())
 
    return "${hexIp}:${hexPort}"
}

def response(evt){
	log.debug "response"
    def msg = parseLanMessage(evt.description)
     log.debug "response(evt) msg body ${msg.body}"
     
      if(msg && msg.body){
      	if(msg.json) {
        	log.debug("is json")
            
            // Room data
            if(msg.json.containsKey("id")){
                 log.debug "childId..."
                //get child device
                def childId = "${app.id}:${msg.json.id}" 

                log.debug "childId ${childId}"

                def childDevice = getChildDevice("${childId}")
                childDevice.parseTstatData(msg.json)
            }
            
            if(msg.json.containsKey("BoilerSettings")){
                log.debug "System data..."
                 
                def type = "Home"
                if(msg.json.containsKey("OverrideType")){
					type = msg.json.OverrideType
                }
                
                log.debug "OverrideType: ${type}"
                
                // update all child devices
                def childDevs = getChildDevices()
                childDevs.each {
                    log.debug "child: ${it.label}"
                    it.parseSystemData(msg.json)
                }
            }
            
        }
        }
}
//parseTstatData

// TODO: implement event handlers
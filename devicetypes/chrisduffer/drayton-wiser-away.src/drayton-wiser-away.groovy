/**
 *  Drayton Wiser Away
 *
 *  Copyright 2018 Chris Evans
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

preferences {

    input("pollingInterval", "number", title:"Polling interval in minutes (1 - 59)",
        required:true, displayDuringSetup:true,  defaultValue:1)
}
 
metadata {
	definition (name: "Drayton Wiser Away", namespace: "chrisduffer", author: "Chris Evans") {
		capability "Polling"
		capability "Refresh"
		capability "Switch"
	}


	simulator {
		// TODO: define status and reply messages here
	}

	tiles (scale: 2){
		multiAttributeTile(name:"switch", type: "generic", width: 6, height: 4, canChangeIcon: true){
			tileAttribute ("device.switch", key: "PRIMARY_CONTROL") {
				attributeState "on", label:'${name}', action:"switch.off", backgroundColor:"#00a0dc", icon: "st.switches.switch.on", nextState:"turningOff"
				attributeState "off", label:'${name}', action:"switch.on", backgroundColor:"#ffffff", icon: "st.switches.switch.off", nextState:"turningOn"
			}
        }
        
        standardTile("refresh", "device.switch", inactiveLabel: false, decoration: "flat", width: 2, height: 2) {
			state "default", label:"", action:"refresh.refresh", icon:"st.secondary.refresh"
		}
	}
    
    main(["switch"])
	details(["switch",
             "refresh"])
}

def updated() {
	log.debug "updated with settings: ${settings}"

    unschedule()
    
    startPollingTask()
    
}

private getPollingInterval() {
    def minutes = settings.pollingInterval?.toInteger()
    if (!minutes) {
	    log.warn "Using default polling interval: 5!"
        minutes = 5
    } else if (minutes < 1) {
        minutes = 1
    } else if (minutes > 59) {
        minutes = 59
    }

    return minutes
}


private startPollingTask() {
    log.debug "startPollingTask()"

    poll()

    Random rand = new Random(now())
    def seconds = rand.nextInt(60)
    def sched = "${seconds} 0/${getPollingInterval()} * * * ?"

    log.debug "Scheduling polling task with \"${sched}\""
    schedule(sched, poll)
}


// parse events into attributes
def parse(String description) {
	log.debug "Parsing '${description}'"
	// TODO: handle 'switch' attribute

}

// handle commands
def poll() {
	log.debug "Executing 'poll'"
    return refresh()
	// TODO: handle 'poll' command
}

def refresh() {
	log.debug "Executing 'refresh'"
    log.debug "refresh()"
    parent.systemRefresh()
	// TODO: handle 'refresh' command
}

def setAway() {
	log.debug "setAway()"
    parent.setAway()
    
    runIn(5,refresh)
}

def setHome() {
	log.debug "setHome()"
    parent.setHome()
    
    runIn(5,refresh)
}

def on() {
	log.debug "Executing 'on'"
    setAway()
	// TODO: handle 'on' command
}

def off() {
	log.debug "Executing 'off'"
    setHome()
	// TODO: handle 'off' command
}

public def parseSystemData(Map sysData) {
	log.trace "TODO parseSystemData"
    log.trace "parseSystemData ${sysData}"
    
    def events = []
    def overrideType = "Home"
    if (sysData.containsKey("OverrideType")) {
        if (sysData.OverrideType == "Away") {
        	overrideType = "Away"
        }
    }
   
    events << createEvent([
            name:   "switch",
            value:  (overrideType == "Away") ? "on" : "off"
        ])
    
    log.debug "sys events: ${events}"
    events.each { event ->
        log.debug "sys event ${event}"
        sendEvent(event)
    }
}
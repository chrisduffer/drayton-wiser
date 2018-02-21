/**
 *  Drayton Wiser WiFi Thermostat.
 *
 *  Based heavily on https://github.com/statusbits/smartthings/blob/master/devicetypes/statusbits/radio-thermostat.src/radio-thermostat.groovy
 */

import groovy.json.JsonSlurper

preferences {
/*

    // FIXME: Android client does not accept "defaultValue" attribute!
    //input("confTcpPort", "number", title:"Thermostat TCP Port",
    //    defaultValue:80, required:true, displayDuringSetup:true)
    //input("pollingInterval", "number", title:"Polling interval in minutes (1 - 59)",
    //    defaultValue:5, required:true, displayDuringSetup:true)
    input("confTcpPort", "number", title:"Thermostat TCP Port (default: 80)",
        required:false, displayDuringSetup:true)
        
    input("confSecret", "string", title:"Thermostat Secret",
        required:false, displayDuringSetup:true)
        */

    input("pollingInterval", "number", title:"Polling interval in minutes (1 - 59)",
        required:true, displayDuringSetup:true,  defaultValue:1)
}

metadata {
    definition (name:"Drayton Wiser Room", namespace:"chrisduffer", author:"chrisduffer@gmail.com") {
        capability "Thermostat"
        capability "Temperature Measurement"
        capability "Sensor"
        capability "Refresh"
        capability "Polling"

        // Custom attributes
        
        // Connection status string TODO is this needed?
        attribute "connection", "string"
        // %age demand
        attribute "demand", "number"        
        // Name from json
        attribute "roomName", "string"
        // output state
        attribute "outputState", "string"
        // overide
        attribute "overrideType", "string"
        // System Override
        attribute "systemOverride", "string"

        // Custom commands
        command "temperatureUp"
        command "temperatureDown"
        command "cancelOverride"
        command "setAway"
        command "setHome"
    }

    tiles(scale:2) {
        multiAttributeTile(name:"thermostat", type:"thermostat", width:6, height:4) {
		    tileAttribute("device.temperature", key:"PRIMARY_CONTROL") {
			    attributeState("default", label:'${currentValue}°', unit:"dC", defaultState:true,
                    backgroundColors:[
                        [value:-12, color:"#153591"],
                        [value:-9, color:"#1e9cbb"],
                        [value:-8, color:"#90d2a7"],
                        [value:-6, color:"#44b621"],
                        [value:-4, color:"#f1d801"],
                        [value:-3, color:"#d04e00"],
                        [value:-1, color:"#bc2323"],
                        [value:0, color:"#153591"],
                        [value:7, color:"#1e9cbb"],
                        [value:15, color:"#90d2a7"],
                        [value:23, color:"#44b621"],
                        [value:28, color:"#f1d801"],
                        [value:35, color:"#d04e00"],
                        [value:36, color:"#bc2323"]
                    ]
                )
	        }
			tileAttribute("device.heatingSetpoint", key:"VALUE_CONTROL") {
				attributeState("VALUE_UP", action:"temperatureUp")
				attributeState("VALUE_DOWN", action:"temperatureDown")
			}
            // label at the bottom
			tileAttribute("device.thermostatOperatingState", key:"OPERATING_STATE") {
				attributeState("idle", backgroundColor:"#44b621", defaultState:true)
				attributeState("heating", backgroundColor:"#ea5462")
			}
			
            // label at the bottom
            tileAttribute("device.thermostatMode", key:"THERMOSTAT_MODE") {
				attributeState("off", label:'${name}')
				attributeState("heat", label:'${name}')
				attributeState("cool", label:'${name}', defaultState:true)
				attributeState("auto", label:'${name}')
			}
            
			tileAttribute("device.heatingSetpoint", key:"HEATING_SETPOINT") {
				attributeState("heatingSetpoint", label:"${currentValue}", unit:"dC", defaultState:true)
			}
        }
        
        valueTile("demand", "device.demand", width:2, height:2) {
            state "demand", label: '${currentValue}% demand'
        }

        standardTile("outputState", "device.outputState", width:2, height:2) {
            state "default", label:'${currentValue}', icon:"st.thermostat.heating-cooling-off", backgroundColor:"#FFFFFF", defaultState:true
            state "On", label:'${currentValue}', icon:"st.thermostat.heat", backgroundColor:"#FFCC99"
        }
        
        standardTile("overrideType", "device.overrideType", width:2, height:2) {
            state "None", label:'OverrideType: ${currentValue}', backgroundColor:"#FFFFFF"
            state "default", label:'Cancel ${currentValue} override', backgroundColor:"#99FF99", action:"cancelOverride", defaultState:true
        }

        standardTile("modeAuto", "device.thermostatMode", width:2, height:2) {
            state "default", label:'${currentValue}', icon:"st.thermostat.auto", backgroundColor:"#FFFFFF", action:"thermostat.auto", defaultState:true
            state "auto", label:'${currentValue}', icon:"st.thermostat.auto", backgroundColor:"#99FF99", action:"thermostat.off"
        }
        
        standardTile("mode", "device.thermostatMode", width:2, height:2) {
            state "default", label:'mode: ${currentValue}'
        }
        
        standardTile("roomName", "device.roomName", width:6, height:2) {
            state "default", label:'${currentValue}'
        }
        
        standardTile("systemOverride", "device.systemOverride", width:2, height:2) {
        	state "Home", label:'Mode: ${currentValue}', backgroundColor:"#99FF99", action:"setAway"
            state "Away", label:'Mode: ${currentValue}', backgroundColor:"#FFCC99", action:"setHome"
            state "default", label:'Mode: ${currentValue}', backgroundColor:"#FFFFFF", defaultState:true
        }

        standardTile("refresh", "device.connection", width:2, height:2, decoration:"flat") {
        //standardTile("refresh", "device.connection", width:2, height:2) {
            state "default", icon:"st.secondary.refresh", backgroundColor:"#FFFFFF", action:"refresh.refresh", defaultState:true
            state "connected", icon:"st.secondary.refresh", backgroundColor:"#44b621", action:"refresh.refresh"
            state "disconnected", icon:"st.secondary.refresh", backgroundColor:"#ea5462", action:"refresh.refresh"
        }

        valueTile("temperature", "device.temperature", width:2, height:2) {
            state "temperature", label:'${currentValue}°', unit:"dC",
                backgroundColors:[
                    [value:-12, color:"#153591"],
                    [value:-9, color:"#1e9cbb"],
                    [value:-8, color:"#90d2a7"],
                    [value:-6, color:"#44b621"],
                    [value:-4, color:"#f1d801"],
                    [value:-3, color:"#d04e00"],
                    [value:-1, color:"#bc2323"],
                    [value:0, color:"#153591"],
                    [value:7, color:"#1e9cbb"],
                    [value:15, color:"#90d2a7"],
                    [value:23, color:"#44b621"],
                    [value:28, color:"#f1d801"],
                    [value:35, color:"#d04e00"],
                    [value:36, color:"#bc2323"]
                ]
        }

        main("temperature")
        details([
            "thermostat",
            "outputState", "mode", "demand",
            "refresh", "overrideType", "systemOverride",
            "roomName"
        ])
    }

    simulator {
        status "Temperature 72.0":      "simulator:true, temp:72.00"
        status "Cooling Setpoint 76.0": "simulator:true, t_cool:76.00"
        status "Heating Setpoint 68.0": "simulator:true, t_cool:68.00"
        status "Thermostat Mode Off":   "simulator:true, tmode:0"
        status "Thermostat Mode Heat":  "simulator:true, tmode:1"
        status "Thermostat Mode Cool":  "simulator:true, tmode:2"
        status "Thermostat Mode Auto":  "simulator:true, tmode:3"
        status "Fan Mode Auto":         "simulator:true, fmode:0"
        status "Fan Mode Circulate":    "simulator:true, fmode:1"
        status "Fan Mode On":           "simulator:true, fmode:2"
        status "State Off":             "simulator:true, tstate:0"
        status "State Heat":            "simulator:true, tstate:1"
        status "State Cool":            "simulator:true, tstate:2"
        status "Fan State Off":         "simulator:true, fstate:0"
        status "Fan State On":          "simulator:true, fstate:1"
        status "Hold Disabled":         "simulator:true, hold:0"
        status "Hold Enabled":          "simulator:true, hold:1"
    }
}

def installed() {
    //log.debug "installed()"

    printTitle()

    // Initialize attributes to default values (Issue #18)
    sendEvent([name:'temperature', value:'21', displayed:false])
    sendEvent([name:'heatingSetpoint', value:'21', displayed:false])
    sendEvent([name:'coolingSetpoint', value:'21', displayed:false])
    sendEvent([name:'thermostatMode', value:'off', displayed:false])
    sendEvent([name:'thermostatFanMode', value:'auto', displayed:false])
    sendEvent([name:'thermostatOperatingState', value:'idle', displayed:false])
    sendEvent([name:'fanState', value:'off', displayed:false])
    sendEvent([name:'hold', value:'off', displayed:false])
    sendEvent([name:'connection', value:'disconnected', displayed:false])
}

def updated() {
	log.debug "updated with settings: ${settings}"

    printTitle()
    unschedule()
    
    
    /*
    if (!settings.confRoom) {
	    log.warn "Room is not set!"
        return
    }

    if (device.currentValue('connection') == null) {
        sendEvent([name:'connection', value:'disconnected', displayed:false])
    }

    if (!settings.confIpAddr) {
	    log.warn "IP address is not set!"
        return
    }
    
    if (!settings.confSecret) {
	    log.warn "Secret is not set!"
        return
    }
    
    
    def port = settings.confTcpPort
    if (!port) {
	    log.warn "Using default TCP port 80!"
        port = 80
    }
    */

	    
    /*    
    def dni = createDNI(settings.confIpAddr, settings.confRoom)
    device.deviceNetworkId = dni
    state.dni = dni;
    state.hostAddress = "${settings.confIpAddr}:${settings.confTcpPort}:${settings.confRoom}"
    */
    state.requestTime = 0
    state.responseTime = 0

    startPollingTask()
    //STATE()
}

def pollingTask() {
    log.debug "pollingTask()"
    
    parent.childPollingTask(device.deviceNetworkId)
    
    /*

    state.lastPoll = now()

    // Check connection status
    def requestTime = state.requestTime ?: 0
    def responseTime = state.responseTime ?: 0
    if (requestTime && (requestTime - responseTime) > 5000) {
        log.warn "No connection!"
        sendEvent([
            name:           'connection',
            value:          'disconnected',
            isStateChange:  true,
            displayed:      true
        ])
    }

    def updated = state.updated ?: 0
    if ((now() - updated) > 10000) {
        //parent.sendHubCommand(apiGet("/data/domain/Room/0"))
        parent.childPollingTask(device.deviceNetworkId)
    }
    */
}

def parse(String message) {
    log.debug "parse(${message})"

    def msg = stringToMap(message)
    if (msg.headers) {
        // parse HTTP response headers
        def headers = new String(msg.headers.decodeBase64())
        def parsedHeaders = parseHttpHeaders(headers)
        //log.debug "parsedHeaders: ${parsedHeaders}"
        if (parsedHeaders.status != 200) {
            log.error "Server error: ${parsedHeaders.reason}"
            return null
        }

        // parse HTTP response body
        if (!msg.body) {
            log.error "HTTP response has no body"
            return null
        }

        def body = new String(msg.body.decodeBase64())
        def slurper = new JsonSlurper()
        def tstat = slurper.parseText(body)
        return parseTstatData(tstat)
    } else if (msg.containsKey("simulator")) {
        // simulator input
        return parseTstatData(msg)
    }

    return null
}

// thermostat.setThermostatMode
def setThermostatMode(mode) {
    log.debug "setThermostatMode(${mode})"
	/*
    switch (mode) {
    case "off":             return off()
    case "heat":            return heat()
    case "cool":            return cool()
    case "auto":            return auto()
    case "emergency heat":  return emergencyHeat()
    }

    log.error "Invalid thermostat mode: \'${mode}\'"
    return null
    */
}

// thermostat.off
def off() {
    log.debug "TODO off()"
	
    /*
    if (device.currentValue("thermostatMode") == "off") {
        return null
    }

    log.info "Setting thermostat mode to 'off'"
    sendEvent([name:"thermostatMode", value:"off"])

    return writeTstatValue('tmode', 0)
    */
}

// thermostat.heat
def heat() {
    log.debug "TODO heat()"

	/*
    if (device.currentValue("thermostatMode") == "heat") {
        return null
    }

    log.info "Setting thermostat mode to 'heat'"
    sendEvent([name:"thermostatMode", value:"heat"])

    return writeTstatValue('tmode', 1)
    */
}

// thermostat.cool
def cool() {
    log.debug "TODO cool()"
	/*
    if (device.currentValue("thermostatMode") == "cool") {
        return null
    }

    log.info "Setting thermostat mode to 'cool'"
    sendEvent([name:"thermostatMode", value:"cool"])

    return writeTstatValue('tmode', 2)
    */
}

// thermostat.auto
def auto() {
    log.debug "TODO auto()"

	/*
    if (device.currentValue("thermostatMode") == "auto") {
        return null
    }

    log.info "Setting thermostat mode to 'auto'"
    sendEvent([name:"thermostatMode", value:"auto"])

    return writeTstatValue('tmode', 3)
    */
}

// thermostat.emergencyHeat
def emergencyHeat() {
    //log.debug "emergencyHeat()"
    log.warn "'emergency heat' mode is not supported"
    return null
}

// thermostat.setThermostatFanMode
def setThermostatFanMode(fanMode) {
    log.debug "TODO setThermostatFanMode(${fanMode})"

	/*
    switch (fanMode) {
    case "auto":        return fanAuto()
    case "circulate":   return fanCirculate()
    case "on":          return fanOn()
    }

    log.error "Invalid fan mode: \'${fanMode}\'"
    return null
    */
}

// thermostat.fanAuto
def fanAuto() {
    log.debug "TODO fanAuto()"
    /*

    if (device.currentValue("thermostatFanMode") == "auto") {
        return null
    }

    log.info "Setting fan mode to 'auto'"
    sendEvent([name:"thermostatFanMode", value:"auto"])

    return writeTstatValue('fmode', 0)
    */
}

// thermostat.fanCirculate
def fanCirculate() {
    //log.debug "fanCirculate()")
    log.warn "Fan 'Circulate' mode is not supported"
    return null
}

// thermostat.fanOn
def fanOn() {
    log.debug "TODO fanOn()"
	/*
    if (device.currentValue("thermostatFanMode") == "on") {
        return null
    }

    log.info "Setting fan mode to 'on'"
    sendEvent([name:"thermostatFanMode", value:"on"])

    return writeTstatValue('fmode', 2)
    */
}

// thermostat.setHeatingSetpoint
def setHeatingSetpoint(temp) {
    log.debug "TODO setHeatingSetpoint(${temp})"

    double minT = 5.0
    double maxT = 30.0
    def scale = getTemperatureScale()
    double t = (scale == "F") ? temperatureFtoC(temp) : temp

    if (t < minT) {
        log.warn "Cannot set heating target below ${minT} °C."
        return null
    } else if (t > maxT) {
        log.warn "Cannot set heating target above ${maxT} °C."
        return null
    }

    log.info "Setting heating setpoint to ${t} °C"

    def ev = [
        name:   "heatingSetpoint",
        value:  (scale == "F") ? temperatureCtoF(t) : t,
        unit:   scale,
    ]

    sendEvent(ev)
    
    setOverride(t)
    return null

    //return writeTstatValue('it_heat', t)
}

// thermostat.setCoolingSetpoint
def setCoolingSetpoint(temp) {
    log.debug "TODO setCoolingSetpoint(${temp})"

/*
    double minT = 36.0
    double maxT = 94.0
    def scale = getTemperatureScale()
    double t = (scale == "C") ? temperatureCtoF(temp) : temp

    t = t.round()
    if (t < minT) {
        log.warn "Cannot set cooling target below ${minT} °F."
        return null
    } else if (t > maxT) {
        log.warn "Cannot set cooling target above ${maxT} °F."
        return null
    }

    log.info "Setting cooling setpoint to ${t} °F"

    def ev = [
        name:   "coolingSetpoint",
        value:  (scale == "F") ? temperatureCtoF(t) : t,
        unit:   scale,
    ]

    sendEvent(ev)

    return writeTstatValue('it_cool', t)
    */
}

// Custom command
def temperatureUp() {
    log.debug "temperatureUp()"

    def step = (getTemperatureScale() == "C") ? 0.5 : 1
    def currentTemp = device.currentValue("heatingSetpoint")?.toFloat()
    log.debug "currentTemp ${currentTemp}"
    
    // off is -20 so set to 19 on up
    def setPoint = (currentTemp < -19) ? 19 : currentTemp + step
    log.debug "setPoint ${setPoint}"
    
    return setHeatingSetpoint(setPoint)
    
    /*
    def mode = device.currentValue("thermostatMode")
    if (mode == "heat") {
        def t = device.currentValue("heatingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current heating setpoint."
            return null
        }
        return setHeatingSetpoint(t + step)
    } else if (mode == "cool") {
        def t = device.currentValue("coolingSetpoint")?.toFloat()
        if (!t) {
            log.error "Cannot get current cooling setpoint."
            return null
        } 
        return setCoolingSetpoint(t + step)
    } else {
        log.warn "Cannot change temperature while in '${mode}' mode."
        return null
    }
    */
}

// Custom command
def temperatureDown() {
    log.debug "TODO temperatureDown()"
    
    def step = (getTemperatureScale() == "C") ? 0.5 : 1
    def currentTemp = device.currentValue("heatingSetpoint")?.toFloat()
    log.debug "currentTemp ${currentTemp}"
    
    def setPoint = currentTemp - step
    log.debug "setPoint ${setPoint}"
    
    return setHeatingSetpoint(setPoint)

}

// Custom command
def holdOn() {
    //log.debug "holdOn()"

    if (device.currentValue("hold") == "on") {
        return null
    }

    log.info "Setting temperature hold to 'on'"
    sendEvent([name:"hold", value:"on"])

    return writeTstatValue("hold", 1)
}

// Custom command
def holdOff() {
    //log.debug "holdOff()"

    if (device.currentValue("hold") == "off") {
        return null
    }

    log.info "Setting temperature hold to 'off'"
    sendEvent([name:"hold", value:"off"])

    return writeTstatValue("hold", 0)
}

def cancelOverride() {
	log.debug "cancelOverride()"
    log.debug "parent.childCancelOverride(${device.deviceNetworkId}"
    parent.childCancelOverride(device.deviceNetworkId)
}

def setOverride(temp){
	log.debug "setOverride()"
	runIn(5, delayedSetOverride, [data: [temp: temp]])
}

def delayedSetOverride(data){
	log.debug "delayedSetOverride()"
    log.debug "parent.childSetOverride(${device.deviceNetworkId} ${data.temp}"
    parent.childSetOverride(device.deviceNetworkId, (data.temp * 10).round())
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


// polling.poll 
def poll() {
    //log.debug "poll()"
    return refresh()
}

// refresh.refresh
def refresh() {
    log.debug "refresh()"
    log.debug "parent.childRefresh(${device.deviceNetworkId})"
    parent.childRefresh(device.deviceNetworkId)
    parent.systemRefresh()
    return null
    //STATE()
    
    /*

    if (!state.dni) {
	    log.warn "DNI is not set! Please enter device IP address and port in settings."
        sendEvent([
            name:           'connection',
            value:          'disconnected',
            isStateChange:  true,
            displayed:      false
        ])

        return null
    }
    
    */
	log.debug("interval")
    def interval = getPollingInterval() * 60
    
    log.debug("elapsed")
    def elapsed =  (now() - state.lastPoll.toInteger()) / 1000
    
    log.debug("if elapsed")
    if (elapsed > (interval + 300)) {
        log.warn "Restarting polling task..."
        unschedule()
        startPollingTask()
    }
    log.debug("send apiget")

	parent.childRefresh(device.deviceNetworkId)
    //return apiGet("/data/domain/Room/0")
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

    pollingTask()

    Random rand = new Random(now())
    def seconds = rand.nextInt(60)
    def sched = "${seconds} 0/${getPollingInterval()} * * * ?"

    log.debug "Scheduling polling task with \"${sched}\""
    schedule(sched, pollingTask)
}


private apiGet(String path) {
    log.debug "apiGet(${path}) parent ip ${parent.ipAddress}"
	
    /*
    if (!updateDNI()) {
        return null
    }*/

    state.requestTime = now()

    def headers = [
        HOST:       parent.ipAddress,
        Accept:     "*/*",
        Secret:		settings.confSecret
    ]

    def httpRequest = [
        method:     'GET',
        path:       path,
        headers:    headers
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}


private apiPost(String path, data) {
    //log.debug "apiPost(${path}, ${data})"

    if (!updateDNI()) {
        return null
    }

    state.requestTime = now()

    def headers = [
        HOST:       state.hostAddress,
        Accept:     "*/*"
    ]

    def httpRequest = [
        method:     'POST',
        path:       path,
        headers:    headers,
        body:       data
    ]

    return new physicalgraph.device.HubAction(httpRequest)
}

private def writeTstatValue(name, value) {
    log.debug "TODO writeTstatValue(${name}, ${value})"

	/*
    def json = "{\"${name}\": ${value}}"
    def hubActions = [
        apiPost("/tstat", json),
        delayHubAction(1500),
        apiGet("/tstat")
    ]

    return hubActions
    */
}

private def delayHubAction(ms) {
    return new physicalgraph.device.HubAction("delay ${ms}")
}

private parseHttpHeaders(String headers) {
    def lines = headers.readLines()
    def status = lines.remove(0).split()

    def result = [
        protocol:   status[0],
        status:     status[1].toInteger(),
        reason:     status[2]
    ]

    return result
}

public def parseSystemData(Map sysData) {
	log.trace "TODO parseSystemData"
    log.trace "parseSystemData ${sysData}"
    
    def events = []
    def overrideType = "Home"
    if (sysData.containsKey("OverrideType")) {
        overrideType = sysData.OverrideType
    }
   
    events << createEvent([
            name:   "systemOverride",
            value:  overrideType
        ])
    
    log.debug "sys events: ${events}"
    events.each { event ->
        log.debug "sys event ${event}"
        sendEvent(event)
    }

}

public def parseTstatData(Map tstat) {
	log.trace "tstat"
    log.trace "tstat data: ${tstat}"

    state.responseTime = now()

    def events = []
    if (tstat.containsKey("error_msg")) {
        log.error "Thermostat error: ${tstat.error_msg}"
        return null
    }

    if (tstat.containsKey("success")) {
        // this is POST response - ignore
        return null
    }

    if (tstat.containsKey("CalculatedTemperature")) {
        events << createEvent([
            name:   "temperature",
            value:  scaleTemperature(tstat.CalculatedTemperature.toFloat()),
            unit:   getTemperatureScale(),
        ])
    }

    if (tstat.containsKey("t_cool")) {
        events << createEvent([
            name:   "coolingSetpoint",
            value:  scaleTemperature(tstat.t_cool.toFloat()),
            unit:   getTemperatureScale(),
        ])
    }

    if (tstat.containsKey("CurrentSetPoint")) {
        events << createEvent([
            name:   "heatingSetpoint",
            value:  scaleTemperature(tstat.CurrentSetPoint.toFloat()),
            unit:   getTemperatureScale(),
        ])
    }

    if (tstat.containsKey("ControlOutputState")) {
        events << createEvent([
            name:   "thermostatOperatingState",
            value:  parseThermostatState(tstat.ControlOutputState)
        ])
    }

    if (tstat.containsKey("fstate")) {
        events << createEvent([
            name:   "fanState",
            value:  parseFanState(tstat.fstate)
        ])
    }

    if (tstat.containsKey("Mode")) {
    	log.debug "got thermostatMode: ${tstat.Mode}"
        events << createEvent([
            name:   "thermostatMode",
            value:  parseThermostatMode(tstat.Mode)
        ])
    }

    if (tstat.containsKey("fmode")) {
        events << createEvent([
            name:   "thermostatFanMode",
            value:  parseFanMode(tstat.fmode)
        ])
    }

    if (tstat.containsKey("PercentageDemand")) {
        events << createEvent([
            name:   "demand",
            value:  parseThermostatDemand(tstat.PercentageDemand)
        ])
    }
    
    if (tstat.containsKey("ControlOutputState")) {
        events << createEvent([
            name:   "outputState",
            value:  parseThermostatControlOutputState(tstat.ControlOutputState)
        ])
    }
    
    if (tstat.containsKey("Name")) {
        events << createEvent([
            name:   "roomName",
            value:  parseThermostatControlOutputState(tstat.Name)
        ])
    }
    
    if (tstat.containsKey("OverrideType")) {
    	def tstatOverride = tstat.OverrideType
    	if (tstat.OverrideType == ""){
        	tstatOverride = "None"
        }
        events << createEvent([
            name:   "overrideType",
            value:  parseThermostatOverrideType(tstatOverride)
        ])
    }
    else {
    	events << createEvent([
            name:   "overrideType",
            value:  "None"
        ])
    }
    
     if (tstat.containsKey("hold")) {
        events << createEvent([
            name:   "hold",
            value:  parseThermostatHold(tstat.hold)
        ])
    }

    events << createEvent([
        name:           'connection',
        value:          'connected',
        isStateChange:  true,
        displayed:      false
    ])

    state.updated = now()

    log.debug "events: ${events}"
    events.each { event ->
        log.debug "event ${event}"
        sendEvent(event)
    }
}

private def parseThermostatState(val) {
	log.debug "parseThermostatState ${val}"
    
    switch (val) {
        case "On":	return "heating"
        default:	return "idle"
    }
}

private def parseFanState(val) {
    def values = [
        "off",      // 0
        "on"        // 1
    ]

    return values[val.toInteger()]
}

private def parseThermostatMode(val) {
	log.debug "parseThermostatMode ${val}"
    
    switch (val) {
        case "Auto":	return "auto"
        default:	return "off"
    }
}

private def parseFanMode(val) {
    def values = [
        "auto",     // 0
        "circulate",// 1 (not supported by CT30)
        "on"        // 2
    ]

    return values[val.toInteger()]
}

private def parseThermostatHold(val) {
    def values = [
        "off",      // 0
        "on"        // 1
    ]

    return values[val.toInteger()]
}

private def parseThermostatDemand(val) {
    return val
}

private def parseThermostatControlOutputState(val) {
	return val
}

private def parseThermostatOverrideType(val) {
	return val
}

private def scaleTemperature(Double temp) {
	log.debug "scaleTemperature: ${temp}"
    temp = temp / 10
    
    
    if (temp < -19 ) {
    	log.debug "setting off"
    	return ''
        }
        
        
    if (getTemperatureScale() == "F") {
        return temperatureCtoF(temp)
    }

    return temp.round(1)
}

private def temperatureCtoF(Double tempC) {
    Double t = (tempC * 1.8) + 32
    return t.round(1)
}

private def temperatureFtoC(Double tempF) {
    Double t = (tempF - 32) / 1.8
    return t.round(1)
}

private String createDNI(ipaddr, port) {
    //log.debug "createDNI(${ipaddr}, ${port})"

    def hexIp = ipaddr.tokenize('.').collect {
        String.format('%02X', it.toInteger())
    }.join()

    def hexPort = String.format('%04X', port.toInteger())
 
    return "${hexIp}:${hexPort}"
}

private updateDNI() {
    if (!state.dni) {
	    log.warn "DNI is not set! Please enter device IP address and port in settings."
        return false
    }
 
    if (state.dni != device.deviceNetworkId) {
	    log.warn "Invalid DNI: ${device.deviceNetworkId}!"
        device.deviceNetworkId = state.dni
    }

    return true
}

private def printTitle() {
    log.info "Radio Thermostat. ${textVersion()}. ${textCopyright()}"
}

private def textVersion() {
    return "Version 0.1"
}

private def textCopyright() {
    return "Copyright © 2018 Chrisduffer"
}

private def STATE() {
    log.trace "state: ${state}"
    log.trace "deviceNetworkId: ${device.deviceNetworkId}"
    log.trace "temperature: ${device.currentValue("temperature")}"
    log.trace "heatingSetpoint: ${device.currentValue("heatingSetpoint")}"
    log.trace "coolingSetpoint: ${device.currentValue("coolingSetpoint")}"
    log.trace "thermostatOperatingState: ${device.currentValue("thermostatOperatingState")}"
    log.trace "thermostatMode: ${device.currentValue("thermostatMode")}"
    log.trace "thermostatFanMode: ${device.currentValue("thermostatFanMode")}"
    log.trace "fanState: ${device.currentValue("fanState")}"
    log.trace "hold: ${device.currentValue("hold")}"
    log.trace "connection: ${device.currentValue("connection")}"
}
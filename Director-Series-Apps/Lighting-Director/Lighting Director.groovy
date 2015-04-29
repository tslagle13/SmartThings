/**
 *  Lighting Director
 *
 *  Version - 1.3
 *  Version - 1.30.1 Modification by Michael Struck - Fixed syntax of help text and titles of scenarios, along with a new icon
 *  Version - 1.40.0 Modification by Michael Struck - Code optimization and added door contact sensor capability		
 *  Version - 1.41.0 Modification by Michael Struck - Code optimization and added time restrictions to each scenario
 *  Version - 2.0  Tim Slagle - Moved to only have 4 slots.  Code was to heavy and needed to be trimmed.
 *  Version - 2.1  Tim Slagle - Moved time interval inputs inline with STs design.
 *  Version - 2.2  Michael Struck - Added the ability to activate switches via the status locks and fixed some syntax issues
 *  Version - 2.5  Michael Struck - Changed the way the app unschedules re-triggered events
 *
 *  Copyright 2015 Tim Slagle & Michael Struck
 *
 *  Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License. You may obtain a copy of the License at:
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *	The original licensing applies, with the following exceptions:
 *		1.	These modifications may NOT be used without freely distributing all these modifications freely
 *			and without limitation, in source form.	 The distribution may be met with a link to source code
 *			with these modifications.
 *		2.	These modifications may NOT be used, directly or indirectly, for the purpose of any type of
 *			monetary gain.	These modifications may not be used in a larger entity which is being sold,
 *			leased, or anything other than freely given.
 *		3.	To clarify 1 and 2 above, if you use these modifications, it must be a free project, and
 *			available to anyone with "no strings attached."	 (You may require a free registration on
 *			a free website or portal in order to distribute the modifications.)
 *		4.	The above listed exceptions to the original licensing do not apply to the holder of the
 *			copyright of the original work.	 The original copyright holder can use the modifications
 *			to hopefully improve their original work.  In that event, this author transfers all claim
 *			and ownership of the modifications to "SmartThings."
 *
 *	Original Copyright information:
 *
 *	Copyright 2014 SmartThings
 *
 *	Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 *	in compliance with the License. You may obtain a copy of the License at:
 *
 *		http://www.apache.org/licenses/LICENSE-2.0
 *
 *	Unless required by applicable law or agreed to in writing, software distributed under the License is distributed
 *	on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the License
 *	for the specific language governing permissions and limitations under the License.
 *
 */
 
definition(
    name: "Lighting Director",
    namespace: "tslagle13",
    author: "Tim Slagle & Michael Struck",
    description: "Control up to 4 sets (scenes) of lights based on motion, door contacts and lux levels.",
    category: "Convenience",
    iconUrl: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Lighting-Director/LightingDirector.png",
    iconX2Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Lighting-Director/LightingDirector@2x.png",
    iconX3Url: "https://raw.githubusercontent.com/MichaelStruck/SmartThings/master/Other-SmartApps/Lighting-Director/LightingDirector@2x.png")



preferences {
    page name:"pageSetup"
    page name:"pageSetupScenarioA"
    page name:"pageSetupScenarioB"
    page name:"pageSetupScenarioC"
    page name:"pageSetupScenarioD"

}

// Show setup page
def pageSetup() {

    def pageProperties = [
        name:       "pageSetup",
        title:      "Status",
        nextPage:   null,
        install:    true,
        uninstall:  true
    ]

	return dynamicPage(pageProperties) {
        section("Setup Menu") {
            href "pageSetupScenarioA", title: getTitle(settings.ScenarioNameA), description: getDesc(settings.ScenarioNameA), state: greyOut(settings.ScenarioNameA)
            href "pageSetupScenarioB", title: getTitle(settings.ScenarioNameB), description: getDesc(settings.ScenarioNameB), state: greyOut(settings.ScenarioNameB)
            href "pageSetupScenarioC", title: getTitle(settings.ScenarioNameC), description: getDesc(settings.ScenarioNameC), state: greyOut(settings.ScenarioNameC)
			href "pageSetupScenarioD", title: getTitle(settings.ScenarioNameD), description: getDesc(settings.ScenarioNameD), state: greyOut(settings.ScenarioNameD)
            }
        section([title:"Options", mobileOnly:true]) {
            label title:"Assign a name", required:false
        }
    }
}

// Show "pageSetupScenarioA" page
def pageSetupScenarioA() {

    def inputLightsA = [
        name:       "A_switches",
        type:       "capability.switch",
        title:      "Control the following switches...",
        multiple:   true,
        required:   false
    ]
    def inputDimmersA = [
        name:       "A_dimmers",
        type:       "capability.switchLevel",
        title:      "Dim the following...",
        multiple:   true,
        required:   false
    ]

    def inputMotionA = [
        name:       "A_motion",
        type:       "capability.motionSensor",
        title:      "Using these motion sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputContactA = [
        name:       "A_contact",
        type:       "capability.contactSensor",
        title:      "Or using these contact sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputLockA = [
        name:       "A_lock",
        type:       "capability.lock",
        title:      "Or using these locks...",
        multiple:   true,
        required:   false
    ]
    
    def inputModeA = [
        name:       "A_mode",
        type:       "mode",
        title:      "Only during the following modes...",
        multiple:   true,
        required:   false
    ]
    
    def inputLevelA = [
        name:       "A_level",
        type:       "enum",
        options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]],
        title:      "Set dimmers to this level",
        multiple:   false,
        required:   false
    ]
    
    def inputTurnOnLuxA = [
        name:       "A_turnOnLux",
        type:       "number",
        title:      "Only run this scene if lux is below...",
        multiple:   false,
        required:   false
    ]
    
    def inputLuxSensorsA = [
        name:       "A_luxSensors",
        type:       "capability.illuminanceMeasurement",
        title:      "On these lux sensors",
        multiple:   false,
        required:   false
    ]
    
    def inputTurnOffA = [
        name:       "A_turnOff",
        type:       "number",
        title:      "Turn off this scene after motion stops or doors close/lock (minutes)...",
        multiple:   false,
        required:   false
    ]
    
    def inputScenarioNameA = [
        name:       "ScenarioNameA",
        type:       "text",
        title:      "Scenario Name",
        multiple:   false,
        required:   false,
        defaultValue: empty
    ]
    
    def pageName = ""
    if (settings.ScenarioNameA) {
        	pageName = settings.ScenarioNameA
   		}
    def pageProperties = [
        name:       "pageSetupScenarioA",
        title:      "${pageName}",
        nextPage:   "pageSetup"
    ]

    return dynamicPage(pageProperties) {
section("Name your scene") {
            input inputScenarioNameA
        }

section("Devices included in the scene") {
            input inputMotionA
            input inputContactA
            input inputLockA
            input inputLightsA
            input inputDimmersA
            }

section("Scene settings") {
            input inputLevelA
            input inputTurnOnLuxA
            input inputLuxSensorsA
            input inputTurnOffA
            input inputModeA
            href "timeIntervalInputA", title: "Only during a certain time", description: getTimeLabel(A_timeStart, A_timeEnd), state: greyedOutTime(A_timeStart, A_timeEnd), refreshAfterSelection:true
            
            }

section("Help") {
            paragraph helpText()
            }
    }
    
}

def pageSetupScenarioB() {

    def inputLightsB = [
        name:       "B_switches",
        type:       "capability.switch",
        title:      "Control the following switches...",
        multiple:   true,
        required:   false
    ]
    def inputDimmersB = [
        name:       "B_dimmers",
        type:       "capability.switchLevel",
        title:      "Dim the following...",
        multiple:   true,
        required:   false
    ]
    
    def inputTurnOnLuxB = [
        name:       "B_turnOnLux",
        type:       "number",
        title:      "Only run this scene if lux is below...",
        multiple:   false,
        required:   false
    ]
    
    def inputLuxSensorsB = [
        name:       "B_luxSensors",
        type:       "capability.illuminanceMeasurement",
        title:      "On these lux sensors",
        multiple:   false,
        required:   false
    ]

    def inputMotionB = [
        name:       "B_motion",
        type:       "capability.motionSensor",
        title:      "Using these motion sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputContactB = [
        name:       "B_contact",
        type:       "capability.contactSensor",
        title:      "Or using these contact sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputLockB = [
        name:       "B_lock",
        type:       "capability.lock",
        title:      "Or using these locks...",
        multiple:   true,
        required:   false
    ]
    
    def inputModeB = [
        name:       "B_mode",
        type:       "mode",
        title:      "Only during the following modes...",
        multiple:   true,
        required:   false
    ]
    
    def inputLevelB = [
        name:       "B_level",
        type:       "enum",
        options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]],
        title:      "Set dimmers to this level",
        multiple:   false,
        required:   false
    ]
    
    def inputTurnOffB = [
        name:       "B_turnOff",
        type:       "number",
        title:      "Turn off this scene after motion stops or doors close/lock  (minutes)...",
        multiple:   false,
        required:   false
    ]
    
    def inputScenarioNameB = [
        name:       "ScenarioNameB",
        type:       "text",
        title:      "Scenario Name",
        multiple:   false,
        required:   false,
        defaultValue: empty
    ]
    
    def pageName = ""
    if (settings.ScenarioNameB) {
        	pageName = settings.ScenarioNameB
   		}
    def pageProperties = [
        name:       "pageSetupScenarioB",
        title:      "${pageName}",
        nextPage:   "pageSetup"
    ]

    return dynamicPage(pageProperties) {
section("Name your scene") {
            input inputScenarioNameB
        }

section("Devices included in the scene") {
            input inputMotionB
			input inputContactB
            input inputLockB
            input inputLightsB
            input inputDimmersB
            }

section("Scene settings") {
            input inputLevelB
            input inputTurnOnLuxB
            input inputLuxSensorsB
            input inputTurnOffB
            input inputModeB
            href "timeIntervalInputB", title: "Only during a certain time", description: getTimeLabel(B_timeStart, B_timeEnd), state: greyedOutTime(B_timeStart, B_timeEnd), refreshAfterSelection:true
            }

section("Help") {
            paragraph helpText()
            }
    }
}

def pageSetupScenarioC() {

    def inputLightsC = [
        name:       "C_switches",
        type:       "capability.switch",
        title:      "Control the following switches...",
        multiple:   true,
        required:   false
    ]
    def inputDimmersC = [
        name:       "C_dimmers",
        type:       "capability.switchLevel",
        title:      "Dim the following...",
        multiple:   true,
        required:   false
    ]

    def inputMotionC = [
        name:       "C_motion",
        type:       "capability.motionSensor",
        title:      "Using these motion sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputContactC = [
        name:       "C_contact",
        type:       "capability.contactSensor",
        title:      "Or using these contact sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputLockC = [
        name:       "C_lock",
        type:       "capability.lock",
        title:      "Or using these locks...",
        multiple:   true,
        required:   false
    ]
    
    def inputModeC = [
        name:       "C_mode",
        type:       "mode",
        title:      "Only during the following modes...",
        multiple:   true,
        required:   false
    ]
    
    def inputLevelC = [
        name:       "C_level",
        type:       "enum",
        options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]],
        title:      "Set dimmers to this level",
        multiple:   false,
        required:   false
    ]
    
    def inputTurnOffC = [
        name:       "C_turnOff",
        type:       "number",
        title:      "Turn off this scene after motion stops or doors close/lock  (minutes)...",
        multiple:   false,
        required:   false
    ]
    
    def inputScenarioNameC = [
        name:       "ScenarioNameC",
        type:       "text",
        title:      "Scenario Name",
        multiple:   false,
        required:   false,
        defaultValue: empty
    ]
    
    def inputTurnOnLuxC = [
        name:       "C_turnOnLux",
        type:       "number",
        title:      "Only run this scene if lux is below...",
        multiple:   false,
        required:   false
    ]
    
    def inputLuxSensorsC = [
        name:       "C_luxSensors",
        type:       "capability.illuminanceMeasurement",
        title:      "On these lux sensors",
        multiple:   false,
        required:   false
    ]
    
    def pageName = ""
    if (settings.ScenarioNameC) {
        	pageName = settings.ScenarioNameC
   		}
    def pageProperties = [
        name:       "pageSetupScenarioC",
        title:      "${pageName}",
        nextPage:   "pageSetup"
    ]

    return dynamicPage(pageProperties) {
        section("Name your scene") {
            input inputScenarioNameC
        }

section("Devices included in the scene") {
            input inputMotionC
            input inputContactC
            input inputLockC
            input inputLightsC
            input inputDimmersC
            }

section("Scene settings") {
            input inputLevelC
            input inputTurnOnLuxC
            input inputLuxSensorsC
            input inputTurnOffC
            input inputModeC
            href "timeIntervalInputC", title: "Only during a certain time", description: getTimeLabel(C_timeStart, C_timeEnd), state: greyedOutTime(C_timeStart, C_timeEnd), refreshAfterSelection:true
            }

section("Help") {
            paragraph helpText()
            }
    }
}

def pageSetupScenarioD() {

    def inputLightsD = [
        name:       "D_switches",
        type:       "capability.switch",
        title:      "Control the following switches...",
        multiple:   true,
        required:   false
    ]
    def inputDimmersD = [
        name:       "D_dimmers",
        type:       "capability.switchLevel",
        title:      "Dim the following...",
        multiple:   true,
        required:   false
    ]

    def inputMotionD = [
        name:       "D_motion",
        type:       "capability.motionSensor",
        title:      "Using these motion sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputContactD = [
        name:       "D_contact",
        type:       "capability.contactSensor",
        title:      "Or using these contact sensors...",
        multiple:   true,
        required:   false
    ]
    
    def inputLockD = [
        name:       "D_lock",
        type:       "capability.lock",
        title:      "Or using these locks...",
        multiple:   true,
        required:   false
    ]
    
    def inputModeD = [
        name:       "D_mode",
        type:       "mode",
        title:      "Only during the following modes...",
        multiple:   true,
        required:   false
    ]
    
    def inputLevelD = [
        name:       "D_level",
        type:       "enum",
        options: [[10:"10%"],[20:"20%"],[30:"30%"],[40:"40%"],[50:"50%"],[60:"60%"],[70:"70%"],[80:"80%"],[90:"90%"],[100:"100%"]],
        title:      "Set dimmers to this level",
        multiple:   false,
        required:   false
    ]
    
    def inputTurnOffD = [
        name:       "D_turnOff",
        type:       "number",
        title:      "Turn off this scene after motion stops, doors close or close/lock  (minutes)...",
        multiple:   false,
        required:   false
    ]
    
    def inputScenarioNameD = [
        name:       "ScenarioNameD",
        type:       "text",
        title:      "Scenario Name",
        multiple:   false,
        required:   false,
        defaultValue: empty
    ]
    
    def inputTurnOnLuxD = [
        name:       "D_turnOnLux",
        type:       "number",
        title:      "Only run this scene if lux is below...",
        multiple:   false,
        required:   false
    ]
    
    def inputLuxSensorsD = [
        name:       "D_luxSensors",
        type:       "capability.illuminanceMeasurement",
        title:      "On these lux sensors",
        multiple:   false,
        required:   false
    ]
    
    def pageName = ""
    if (settings.ScenarioNameD) {
        	pageName = settings.ScenarioNameD
   		}
    def pageProperties = [
        name:       "pageSetupScenarioD",
        title:      "${pageName}",
        nextPage:   "pageSetup"
    ]

    return dynamicPage(pageProperties) {
        section("Name your scene") {
            input inputScenarioNameD
        }

section("Devices included in the scene") {
            input inputMotionD
          	input inputContactD
            input inputLockD
            input inputLightsD
            input inputDimmersD
            }

section("Scene settings") {
            input inputLevelD
            input inputTurnOnLuxD
            input inputLuxSensorsD
            input inputTurnOffD
            input inputModeD
            href "timeIntervalInputD", title: "Only during a certain time", description: getTimeLabel(D_timeStart, D_timeEnd), state: greyedOutTime(D_timeStart, D_timeEnd), refreshAfterSelection:true
            }

section("Help") {
            paragraph helpText()
            }
    }
}

def installed() {

    initialize()
}

def updated() {

    unschedule()
    unsubscribe()
    initialize()
}

def initialize() {
settings.A_timerStart = false
settings.B_timerStart = false
settings.C_timerStart = false
settings.D_timerStart = false

if(settings.A_motion) {
	subscribe(settings.A_motion, "motion", onEventA)
}

if(settings.A_contact) {
	subscribe(settings.A_contact, "contact", onEventA)
}

if(settings.A_lock) {
	subscribe(settings.A_lock, "lock", onEventA)
}

if(settings.B_motion) {
	subscribe(settings.B_motion, "motion", onEventB)
}

if(settings.B_contact) {
	subscribe(settings.B_contact, "contact", onEventB)
}

if(settings.B_lock) {
	subscribe(settings.B_lock, "lock", onEventB)
}

if(settings.C_motion) {
	subscribe(settings.C_motion, "motion", onEventC)
}

if(settings.C_contact) {
	subscribe(settings.C_contact, "contact", onEventC)
}

if(settings.C_lock) {
	subscribe(settings.C_lock, "lock", onEventC)
}

if(settings.D_motion) {
	subscribe(settings.D_motion, "motion", onEventD)
}

if(settings.D_contact) {
	subscribe(settings.D_contact, "contact", onEventD)
}

if(settings.D_lock) {
	subscribe(settings.D_lock, "lock", onEventD)
}
}

def onEventA(evt) {

if ((settings.A_mode==null || settings.A_mode.contains(location.mode)) && getTimeOk (A_timeStart, A_timeEnd)){
if ((settings.A_luxSensors == null) || (settings.A_luxSensors.latestValue("illuminance") <= A_turnOnLux)){
def A_levelOn = settings.A_level as Integer
def delayA = settings.A_turnOff * 60
def motionDetected = false
def contactDetected = false
def unlockDetected = false

if (settings.A_motion) {
	if (settings.A_motion.latestValue("motion").contains("active")) {
		motionDetected = true
	}
}

if (settings.A_contact) {
	if (settings.A_contact.latestValue("contact").contains("open")) {
		contactDetected = true
	}
}

if (settings.A_lock) {
	if (settings.A_lock.latestValue("lock").contains("unlocked")) {
		unlockDetected = true
	}
}

if (motionDetected || contactDetected || unlockDetected ) {
		log.debug("Motion, Door Open or Unlock Detected Running '${settings.ScenarioNameA}'")
		settings.A_dimmers?.setLevel(A_levelOn)
		settings.A_switches?.on()
        if (settings.A_timerStart){
        	unschedule(delayTurnOffA)
            settings.A_timerStart = false
        }
}
else {
    	if (settings.A_turnOff) {
		runIn(delayA, "delayTurnOffA")
        settings.A_timerStart = true
        }
        
        else {
        settings.A_switches?.off()
		settings.A_dimmers?.setLevel(0)
        }
	
}
}
}
else{
log.debug("Motion, Contact or Unlock detected outside of mode or time restriction.  Not running mode.")
}
}

def delayTurnOffA(){
	settings.A_switches?.off()
	settings.A_dimmers?.setLevel(0)
	settings.A_timerStart = false
}

def onEventB(evt) {

if ((settings.B_mode==null || settings.B_mode.contains(location.mode)) && getTimeOk (B_timeStart, B_timeEnd)){
if ((settings.B_luxSensors == null) || (settings.B_luxSensors.latestValue("illuminance") <= B_turnOnLux)){
def B_levelOn = settings.B_level as Integer
def delayB = settings.B_turnOff * 60
def motionDetected = false
def contactDetected = false
def unlockDetected = false

if (settings.B_motion) {
	if (settings.B_motion.latestValue("motion").contains("active")) {
		motionDetected = true
	}
}

if (settings.B_contact) {
	if (settings.B_contact.latestValue("contact").contains("open")) {
		contactDetected = true
	}
}

if (settings.B_lock) {
	if (settings.B_lock.latestValue("lock").contains("unlocked")) {
		unlockDetected = true
	}
}

if (motionDetected || contactDetected || unlockDetected ) {
		log.debug("Motion, Door Open or Unlock Detected Running '${settings.ScenarioNameB}'")
		settings.B_dimmers?.setLevel(B_levelOn)
		settings.B_switches?.on()
        if (settings.B_timerStart){
        	unschedule(delayTurnOffB)
            settings.B_timerStart = false
        }
}
else {
    	if (settings.B_turnOff) {
		runIn(delayB, "delayTurnOffB")
        settings.B_timerStart = true
        }
        
        else {
        settings.B_switches?.off()
		settings.B_dimmers?.setLevel(0)
        }
	
}
}
}
else{
log.debug("Motion, Contact or Unlock detected outside of mode or time restriction.  Not running mode.")
}
}

def delayTurnOffB(){
	settings.B_switches?.off()
	settings.B_dimmers?.setLevel(0)
	settings.B_timerStart = false
}


def onEventC(evt) {


if ((settings.C_mode==null || settings.C_mode.contains(location.mode)) && getTimeOk (C_timeStart, C_timeEnd)){
if ((settings.C_luxSensors == null) || (settings.C_luxSensors.latestValue("illuminance") <= C_turnOnLux)){
def C_levelOn = settings.C_level as Integer
def delayC = settings.C_turnOff * 60
def motionDetected = false
def contactDetected = false
def unlockDetected = false

if (settings.C_motion) {
	if (settings.C_motion.latestValue("motion").contains("active")) {
		motionDetected = true
	}
}

if (settings.C_contact) {
	if (settings.C_contact.latestValue("contact").contains("open")) {
		contactDetected = true
	}
}

if (settings.C_lock) {
	if (settings.C_lock.latestValue("lock").contains("unlocked")) {
		unlockDetected = true
	}
}

if (motionDetected || contactDetected || unlockDetected ) {
		log.debug("Motion, Door Open or Unlock Detected Running '${settings.ScenarioNameC}'")
		settings.C_dimmers?.setLevel(C_levelOn)
		settings.C_switches?.on()
        if (settings.C_timerStart){
        	unschedule(delayTurnOffC) 
            settings.C_timerStart = false
        }
}
else {
    	if (settings.C_turnOff) {
		runIn(delayC, "delayTurnOffC")
        settinngs.C_timerStart = true
        }
        
        else {
        settings.C_switches?.off()
		settings.C_dimmers?.setLevel(0)
        }
	
}
}
}
else{
log.debug("Motion, Contact or Unlock detected outside of mode or time restriction.  Not running mode.")
}
}

def delayTurnOffC(){
	settings.C_switches?.off()
	settings.C_dimmers?.setLevel(0)
	settinngs.C_timerStart = false
}


def onEventD(evt) {

if ((settings.D_mode==null || settings.D_mode.contains(location.mode)) && getTimeOk (D_timeStart, D_timeEnd)){
if ((settings.D_luxSensors == null) || (settings.D_luxSensors.latestValue("illuminance") <= D_turnOnLux)){
def D_levelOn = settings.D_level as Integer
def delayD = settings.D_turnOff * 60
def motionDetected = false
def contactDetected = false
def unlockDetected = false

if (settings.D_motion) {
	if (settings.D_motion.latestValue("motion").contains("active")) {
		motionDetected = true
	}
}

if (settings.D_contact) {
	if (settings.D_contact.latestValue("contact").contains("open")) {
		contactDetected = true
	}
}

if (settings.D_lock) {
	if (settings.D_lock.latestValue("lock").contains("unlocked")) {
		unlockDetected = true
	}
}

if (motionDetected || contactDetected || unlockDetected ) {
		log.debug("Motion, Door Open or Unlock Detected Running '${settings.ScenarioNameD}'")
		settings.D_dimmers?.setLevel(D_levelOn)
		settings.D_switches?.on()
        if (settings.D_timerStart){
        	unschedule(delayTurnOffD) 
            settings.D_timerStart = false
        }
}
else {
    	if (settings.D_turnOff) {
		runIn(delayD, "delayTurnOffD")
        settings.D_timerStart = true
        }
        
        else {
        settings.D_switches?.off()
		settings.D_dimmers?.setLevel(0)
      
        }
	
}
}
}
else{
log.debug("Motion, Contact or Unlock detected outside of mode or time restriction.  Not running mode.")
}
}

def delayTurnOffD(){
	settings.D_switches?.off()
	settings.D_dimmers?.setLevel(0)
	settinngs.D_timerStart = false
}

private def helpText() {
	def text =
    	"Select motion sensors, contact sensors or locks to control a set of lights. " +
        "Each scenario can control dimmers and switches but can also be " +
        "restricted to modes or between certain times and turned off after "
        "motion stops, doors close or lock."
	text
}

def greyOut(scenario){
	def result = ""
    if (scenario) {
    	result = "complete"	
    }
    result
}

def greyedOutTime(start, end){
	def result = ""
    if (start || end) {
    	result = "complete"	
    }
    result
}

def getTitle(scenario) {
	def title = "Empty"
	if (scenario) {
		title = scenario
    }
	title
}

def getDesc(scenario) {
	def desc = "Tap to create a scene"
	if (scenario) {
		desc = "Tap to edit scene"
    }
	desc	
}

private getTimeOk(startTime, endTime) {
	def result = true
	if (startTime && endTime) {
		def currTime = now()
		def start = timeToday(startTime).time
		def stop = timeToday(endTime).time
		result = start < stop ? currTime >= start && currTime <= stop : currTime <= stop || currTime >= start
	}
	log.trace "timeOk = $result"
	result
}

def getTimeLabel(start, end){
	def timeLabel = "Tap to set"
	
    if(start && end){
    	timeLabel = "Between" + " " + hhmm(start) + " "  + "and" + " " +  hhmm(end)
    }
    else if (start) {
		timeLabel = "Start at" + " " + hhmm(start)
    }
    else if(end){
    timeLabel = "End at" + hhmm(end)
    }
	timeLabel	
}

private hhmm(time, fmt = "h:mm a")
{
	def t = timeToday(time, location.timeZone)
	def f = new java.text.SimpleDateFormat(fmt)
	f.setTimeZone(location.timeZone ?: timeZone(time))
	f.format(t)
}


page(name: "timeIntervalInputA", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "A_timeStart", "time", title: "Starting (both are required)", required: false, refreshAfterSelection:true
			input "A_timeEnd", "time", title: "Ending (both are required)", required: false, refreshAfterSelection:true
		}
        }  
page(name: "timeIntervalInputB", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "B_timeStart", "time", title: "Starting (both are required)", required: false, refreshAfterSelection:true
			input "B_timeEnd", "time", title: "Ending (both are required)", required: false, refreshAfterSelection:true
		}
        }  
page(name: "timeIntervalInputC", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "C_timeStart", "time", title: "Starting (both are required)", required: false, refreshAfterSelection:true
			input "C_timeEnd", "time", title: "Ending (both are required)", required: false, refreshAfterSelection:true
		}
        }         
page(name: "timeIntervalInputD", title: "Only during a certain time", refreshAfterSelection:true) {
		section {
			input "D_timeStart", "time", title: "Starting (both are required)", required: false, refreshAfterSelection:true
			input "D_timeEnd", "time", title: "Ending (both are required)", required: false, refreshAfterSelection:true
		}
        }          

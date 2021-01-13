//Validator

//Classpath
package pipeline.utils

def isValidStage(String stage_pipeline, String stage_param) {

    def stages_to_validate = stage_param.tokenize(';')
    
    if(stages_to_validate.size()==0){
        println "DEBUG: Se inició el pipeline sin restringir los stages, Se inicia stage [${stage_pipeline}]"
        return true
    }
    
    if(stages_to_validate.contains(stage_pipeline)){
        println "DEBUG: Se inició el pipeline con parámetro stages [${stage_param}], Se inicia stage [${stage_pipeline}]"
        return true
    }

    println "DEBUG: stage [${stage_pipeline}] no solicitado, se saltará..."

    return false
}

def getNameFlow(branch_name){

    
    if (env.GIT_BRANCH.contains('feature') || env.GIT_BRANCH.contains('develop')) {
        return "Integracion Continua"
    }
    else if (env.GIT_BRANCH.contains('release')) {
        return "Despliegue Continuo"       
    }
    else if (env.GIT_BRANCH.contains('master') || env.GIT_BRANCH.contains('main')) {
        return "Error: No es posible ejecutar Integración Continua sobre master/main"  
    }
    else {
        return "Error: No se reconoce flujo"
    }

}

def isValidReleaseVersion(String version) {

    if (version =~ '\\d+\\-\\d+\\-\\d+') {
        return true
    }
    else {
        return false
    }
}

def technologyType(String nombreProyecto) {

    if (nombreProyecto.contains('ms')) {
        return "Tecnologia Microservicios"
    }
    else if (nombreProyecto.contains('front')) {
        return "Tecnologia Frontend"
    }
    else if (nombreProyecto.contains('bff')) {
        return "Tecnologia Backend for Fronend"
    }
    else {
        return "Tecnologia No Reconocida"
    }

}

def validateTool() {

    def fileTool = (env.TOOL == 'gradle') ? 'build.gradle' : 'pom.xml'

    println "DEBUG: fileTool ->" + fileTool

    if (fileExists(fileTool)) {
        return true
    } 
    else {
        return false
    }
}




return this;




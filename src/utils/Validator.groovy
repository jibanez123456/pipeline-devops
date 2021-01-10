//Validator

//Classpath
package pipeline.utils

def isValidStage(String stage_pipeline, String stage_param) {

    def stages_to_validate = stage_param.tokenize(';')
    
    if(stages_to_validate.size()==0){
        println "Se inició el pipeline sin restringir los stages, Se inicia stage [${stage_pipeline}]"
        return true
    }
    
    if(stages_to_validate.contains(stage_pipeline)){
        println "Se inició el pipeline con parámetro stages [${stage_param}], Se inicia stage [${stage_pipeline}]"
        return true
    }

    println "stage [${stage_pipeline}] no solicitado, se saltará..."

    return false
}

def getNameFlow(branch_name){
    if(branch_name.matches("(.*)feature(.*)") || branch_name == "(.*)develop"){
        return "Integracion Continua"
    }else{
        if(branch_name.matches("(.*)release(.*)")){
            return "Despliegue Continuo"
        }else{
            return "No se reconoce flujo"
        }
    }
}

return this;
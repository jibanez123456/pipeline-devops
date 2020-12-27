def call(){

	pipeline {
    agent any

	parameters { 
		choice(name: 'tool', choices: ['gradle', 'maven'], description: 'Selección herramienta de Construcción:')
		string(name: 'stage', defaultValue: '', description: 'Ingrese parametro->stage a ejecutar:')
	}

	// agregar la validación del parametro stage: vacio, un stage o todos los stages
    
    stages {
    	stage('Validacion') {
            steps {
                script {
                		env.ETAPA = 'Validacion'
						println "INFO: Validacion parametros"

						String[] params_stage_list = params.stage.split(';')
						for( String values : params_stage_list )
      						println(values);

      					if (params_stage_list.length() >= 1) {
							println "INFO: Cantidad de Etapas -> OK!"		
      					}
      					else {
							error 'Parametros invalidos, ejecución interrumpida: Cantidad de Steps'

      					}
						//caso Gradle 
						if (params.stage.contains('Build-Test') || params.stage.contains('Sonar') || params.stage.contains('Run') || params.stage.contains('Test') || params.stage.contains('Nexus')) {
							println "INFO: Etapas validas -> OK!"		
						}
						else {
							error 'Parametros invalidos, ejecución interrumpida: Steps no reconocidos'
						}

						// caso Maven


                }
            }
    	}
        stage('Pipeline') {
            steps {
                script {
                	env.ETAPA = ''
                	env.PARAM_STAGE = params.stage
                	
                	if (params.stage.isEmpty()) {
						// ejecutar todos los steps
						println "INFO: Ejecución de TODOS los stages"

						params.tool

						if (params.tool == 'gradle') { 
								//def ejecucion = load 'gradle.groovy'
								//ejecucion.call()
								gradle.call()
						}
						else {
								//def ejecucion = load 'maven.groovy'
								//ejecucion.call()
								maven.call()
						}
					}
					else {
						// ejecutar el o los stages ingresados
						println "INFO: Ejecución de uno o mas stages"
						if (params.tool == 'gradle') { 
								gradle.call()
						}
						else {
								maven.call()
						}
					}
				}
            }
        }
    }	

    post {

    	success {
    		 slackSend message: '[Jose Ibanez] [' + env.JOB_NAME + '] [' + params.tool  + '] [Ejecucion exitosa] ', teamDomain: 'devops-usach-2020', tokenCredentialId: 'slacktoken'
    	}

    	failure {
    		 slackSend message: '[Jose Ibanez] [' + env.JOB_NAME + '] [' + params.tool  + '] [Ejecucion fallida en stage:' + env.ETAPA + '] ', teamDomain: 'devops-usach-2020', tokenCredentialId: 'slacktoken'
    	}
    }
}
  
  

}

return this;

/*
	forma de invocación de método call:
	def ejecucion = load 'script.groovy'
	ejecucion.call()
*/

def call(){

    pipeline {
        agent any

        parameters { 
            choice(name: 'TOOL', choices: ['maven'], description: 'Selecciona herramienta') 
            string(name: 'stage', defaultValue: '', description: 'Selecciona stage a ejecutar')
            string(name: 'releaseVersion', defaultValue: '', description: 'Ingresar version formato: {major}-{minor}-{patch}, ejemplo: 0-0-0')
        }
        
        stages {
            stage('Pipeline') {
                steps {
                    script{
                        figlet params.TOOL

                        env.STAGE

                        env.RELEASE_VERSION = params.releaseVersion

                        if (params.TOOL == 'gradle'){
                            gradle.call(params.stage, env.GIT_BRANCH)
                        } else {
                            maven.call(params.stage, env.GIT_BRANCH)
                        }

                    }
                }
            }
        }

        post {

    	success {
    		 slackSend message: '[Jose Ibanez] [' + env.JOB_NAME + '] [' + env.TOOl  + '] [Ejecucion exitosa] ', teamDomain: 'devops-usach-2020', tokenCredentialId: 'slacktoken'
    	}

    	failure {
    		 slackSend message: '[Jose Ibanez] [' + env.JOB_NAME + '] [' + env.TOOL  + '] [Ejecucion fallida en stage:' + env.STAGE + '] ', teamDomain: 'devops-usach-2020', tokenCredentialId: 'slacktoken'
    	}
    }
    }  

}

return this;
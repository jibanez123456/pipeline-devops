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
        }
        
        stages {
            stage('Pipeline') {
                steps {
                    script{
                        figlet params.TOOL

                        env.STAGE

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
                slackSend color: 'good', message: "[Pablo Rocco][${env.JOB_NAME}][${env.TOOL}] Ejecución Exitosa", teamDomain: 'devops-usach-2020', tokenCredentialId: 'slack_token'
            }

            failure {
                slackSend color: 'danger', message: "[Pablo Rocco][${env.JOB_NAME}][${env.TOOL}] Ejecución fallida en [${env.STAGE}]", teamDomain: 'devops-usach-2020', tokenCredentialId: 'slack_token'
            }

        }
    }  

}

return this;
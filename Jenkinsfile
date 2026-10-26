pipeline {

    agent any

    environment {
        DOCKER_IMAGE = "sathvikjakkampudi/calculator-devops:${BUILD_NUMBER}"
        DOCKER_CREDENTIALS = "dockerhub-credentials"
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                sh 'mvn clean package -DskipTests'
            }
        }

        stage('Test') {
            steps {
                sh 'mvn test'
            }
        }

        stage('Docker Build') {
            steps {
                sh 'docker build -t ${DOCKER_IMAGE} .'
            }
        }

        stage('Docker Push') {
            steps {
                withCredentials([
                    usernamePassword(
                        credentialsId: "${DOCKER_CREDENTIALS}",
                        usernameVariable: 'DOCKER_USERNAME',
                        passwordVariable: 'DOCKER_PASSWORD'
                    )
                ]) {
                    sh '''
                        echo "$DOCKER_PASSWORD" | docker login \
                            -u "$DOCKER_USERNAME" \
                            --password-stdin

                        docker push "$DOCKER_IMAGE"

                        docker logout
                    '''
                }
            }
        }

        stage('Deploy') {
            steps {
                sh '''
                    CALCULATOR_IMAGE="$DOCKER_IMAGE" \
                    docker compose -f docker-compose.yml pull calculator

                    CALCULATOR_IMAGE="$DOCKER_IMAGE" \
                    docker compose -f docker-compose.yml up -d --no-build
                '''
            }
        }

        stage('Health Verify') {
            steps {
                sh '''
                    echo "Waiting for calculator service..."

                    for i in $(seq 1 30); do
                        if curl -fsS http://localhost:8082/actuator/health; then
                            echo ""
                            echo "Calculator service is healthy."
                            docker compose -f docker-compose.yml ps
                            exit 0
                        fi

                        echo "Service not ready yet. Waiting..."
                        sleep 2
                    done

                    echo "Health check failed."
                    docker compose -f docker-compose.yml ps
                    exit 1
                '''
            }
        }
    }

    post {

        always {
            junit 'target/surefire-reports/*.xml'
        }

        success {
            mail to: 'sathvikjakkampudi2006@gmail.com',
                 subject: "SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """Build ${env.BUILD_NUMBER} completed successfully.

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Status: SUCCESS

Jenkins build URL:
${env.BUILD_URL}
"""
        }

        failure {
            mail to: 'sathvikjakkampudi2006@gmail.com',
                 subject: "FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
                 body: """Build ${env.BUILD_NUMBER} failed.

Job: ${env.JOB_NAME}
Build: #${env.BUILD_NUMBER}
Status: FAILURE

Check the Jenkins console output:
${env.BUILD_URL}
"""
        }
    }
}



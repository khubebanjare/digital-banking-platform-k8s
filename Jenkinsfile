pipeline {
    agent any

    tools {
        jdk 'jdk21'
    }

    environment {
        GRADLE_OPTS = '-Dorg.gradle.daemon=false -Dorg.gradle.jvmargs="-Xmx2048m -XX:MaxMetaspaceSize=512m"'
        JAVA_HOME = '/opt/jdk-21.0.11'
    }

    options {
        skipDefaultCheckout(true)
        timeout(time: 30, unit: 'MINUTES')
        retry(2)
        disableConcurrentBuilds()
        buildDiscarder(logRotator(numToKeepStr: '10'))
    }

    stages {

        stage('Checkout') {
            steps {
                checkout scm
            }
        }

        stage('Build') {
            steps {
                retry(3) {
                    timeout(time: 10, unit: 'MINUTES') {
                        sh './gradlew :auth-service:clean :auth-service:build --no-daemon --stacktrace'
                    }
                }
            }
        }

        stage('Test') {
            steps {
                retry(2) {
                    timeout(time: 15, unit: 'MINUTES') {
                        sh './gradlew :auth-service:test --no-daemon --stacktrace'
                    }
                }
            }
        }

        stage('JaCoCo Coverage') {
            steps {
                retry(2) {
                    timeout(time: 10, unit: 'MINUTES') {
                        sh './gradlew :auth-service:jacocoTestReport --no-daemon'
                    }
                }
            }
        }

        stage('Mutation Testing') {
            steps {
                retry(2) {
                    timeout(time: 15, unit: 'MINUTES') {
                        sh './gradlew :auth-service:pitest --no-daemon'
                    }
                }
            }
        }

        stage('SonarQube Analysis') {
            steps {
                retry(2) {
                    timeout(time: 10, unit: 'MINUTES') {
                        withSonarQubeEnv('SonarQube') {
                            sh './gradlew :auth-service:sonar --no-daemon'
                        }
                    }
                }
            }
        }
    }

//    post {
//        always {
//
//            junit(
//                    allowEmptyResults: true,
//                    skipPublishingChecks: true,
//                    testResults: '**/build/test-results/test/*.xml'
//            )
//
//            archiveArtifacts(
//                    artifacts: 'auth-service/build/reports/jacoco/test/html/**',
//                    allowEmptyArchive: true
//            )
//
//            archiveArtifacts(
//                    artifacts: 'auth-service/build/reports/pitest/**',
//                    allowEmptyArchive: true
//            )
//
//            publishHTML([
//                    allowMissing: false,
//                    alwaysLinkToLastBuild: true,
//                    keepAll: true,
//                    reportDir: 'auth-service/build/reports/jacoco/test/html',
//                    reportFiles: 'index.html',
//                    reportName: 'JaCoCo Coverage Report'
//            ])
//
//            echo "==================================="
//            echo "QUALITY REPORTS"
//            echo "==================================="
//
//            echo "JaCoCo Report:"
//            echo "${env.BUILD_URL}JaCoCo_20Coverage_20Report/"
//
//            echo "PIT Mutation Report:"
//            echo "${env.BUILD_URL}artifact/auth-service/build/reports/pitest/index.html"
//
//            echo "SonarQube Dashboard:"
//            echo "http://localhost:9000/dashboard?id=auth-service"
//
//            cleanWs()
//        }
//        success {
//            emailext(
//                    subject: "✅ SUCCESS: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
//                    body: """
//                Build Successful
//
//                Project: ${env.JOB_NAME}
//                Build Number: ${env.BUILD_NUMBER}
//
//                Build URL:
//                ${env.BUILD_URL}
//            """,
//                    to: "info.khube@gmail.com"
//            )
//        }
//
//        failure {
//            emailext(
//                    subject: "❌ FAILURE: ${env.JOB_NAME} #${env.BUILD_NUMBER}",
//                    body: """
//                Build Failed
//
//                Project: ${env.JOB_NAME}
//                Build Number: ${env.BUILD_NUMBER}
//
//                Check Console Output:
//                ${env.BUILD_URL}
//
//                Possible Causes:
//                - Unit Test Failure
//                - JaCoCo Threshold Failure
//                - PIT Mutation Failure
//                - SonarQube Quality Gate Failure
//                - Docker Build Failure
//                - Kubernetes Deployment Failure
//            """,
//                    to: "info.khube@gmail.com"
//            )
//        }
        post {
            always {
                echo "POST BLOCK EXECUTED"
            }

            success {
                echo "SUCCESS BLOCK EXECUTED"

                emailext(
                        subject: "SUCCESS TEST",
                        body: "SUCCESS TEST",
                        to: "info.khube@gmail.com"
                )
            }

            failure {
                echo "FAILURE BLOCK EXECUTED"

                emailext(
                        subject: "FAILURE TEST",
                        body: "FAILURE TEST",
                        to: "info.khube@gmail.com"
                )
            }
        }
   // }

}

pipeline {
agent any

tools {
    jdk 'jdk21'
}

environment {
    GRADLE_OPTS = '-Dorg.gradle.daemon=false'
}

stages {

    stage('Checkout') {
        steps {
            checkout scm
        }
    }

    stage('Build') {
        steps {
            sh './gradlew clean build'
        }
    }

    stage('Unit Tests') {
        steps {
            sh './gradlew test'
        }
    }

    stage('Code Coverage') {
        steps {
            sh './gradlew jacocoTestReport'
        }
    }

    stage('SonarQube Analysis') {
        steps {
            withSonarQubeEnv('SonarQube') {
                sh './gradlew sonar'
            }
            echo 'SonarQube stage will be configured next'
        }
    }
}

post {
    always {
        junit '*build/test-results/test.xml'
    }

    success {
        echo 'Build completed successfully'
    }

    failure {
        echo 'Build failed'
    }
}

}

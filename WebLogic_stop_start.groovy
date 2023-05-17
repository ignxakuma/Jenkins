pipeline
{
    agent any
    /*
    {
        node{
        label 'Weblogic-server'
        }
    }
    */
    parameters{
        choice (choices: ['Stop', 'Start'], name: 'StopStart')
        choice (choices: ['Dev', 'Test', 'Prod'], name:'Env')
        
    }
    options{
        timestamps()
        timeout(time: 300, unit: 'SECONDS')
    }

    stages{
        stage("Stopping Weblogic")
        {
            when{
                expression
                {
                    return params.StopStart == 'Stop'
                    return params.Env == 'Dev'
                }
                }
            steps{
                script{
                    sh '''
                    ssh -tt -o StrictHostKeyChecking=no oracle@3.89.99.228  << EOF
                    export MW_HOME=/opt/oracle/middleware
                    export WLS_HOME=/opt/oracle/middleware/wlserver
                    export WL_HOME=/opt/oracle/middleware/wlserver
                    /opt/oracle/middleware/user_projects/domains/basicWLSDomain/bin/stopWebLogic.sh
                    sleep 60
                    exit
                    EOF '''
                }
            }
        }
        stage("Starting Weblogic")
        {
            when{
                expression
                {
                    return params.StopStart == 'Start'
                    return params.Env == 'Dev'
                }
                }
            steps{
                script{
                    sh '''
                    ssh -tt -o StrictHostKeyChecking=no oracle@3.89.99.228  << EOF
                    export MW_HOME=/opt/oracle/middleware
                    export WLS_HOME=/opt/oracle/middleware/wlserver
                    export WL_HOME=/opt/oracle/middleware/wlserver
                    nohup /opt/oracle/middleware/user_projects/domains/basicWLSDomain/startWebLogic.sh &
                    sleep 120
                    exit
                    EOF '''
                }
            }
        }
    }
}

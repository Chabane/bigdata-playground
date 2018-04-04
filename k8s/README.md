# Deploying the application on Kubernetes

Note : We are currently working on this file ...

WE are going to use kubernetes to deploy our application. Why kubernetes? 
We will answer this question in the following days ...

## Setup environment
We are going to need to install:

Docker (https://docs.docker.com/docker-for-windows/)
VirtualBox (https://www.virtualbox.org/wiki/Downloads)
Minikube (https://github.com/kubernetes/minikube)
Kubectl (here I’m using version 1.9.0) (https://kubernetes.io/docs/tasks/tools/install-kubectl/)

### Reusing the Docker daemon
To be able to work with the docker daemon on your mac/linux host use the docker-env command in your shell:

```bash
$ minikube docker-env
 ```
 you will have something like that : 

```bash
$ minikube docker-env
$Env:DOCKER_TLS_VERIFY = "1"
$Env:DOCKER_HOST = "tcp://192.168.99.100:2376"
$Env:DOCKER_CERT_PATH = "C:\Users\toto\.minikube\certs"
$Env:DOCKER_API_VERSION = "1.23"
# Run this command to configure your shell:
# & minikube docker-env | Invoke-Expression
 ```

As you can see, in order to configure our shell, we have to run:

```bash
$ minikube docker-env | Invoke-Expression
 ```

 Now that we have everything setup, let's build & deploy our application.

## Build the needed images
First we are going to build the images we want to deploy. For the moment, we are only going to deploy the webapp aplication (angular/nodeJS), with the mongoDB (the webapp needs it to run)
and the spark batch that will inject the list of airports in mongoDB after it starts.

Let's start ...
 
 First we build our webapp application

```bash
$ cd webapp/ 
$ docker build -t webapp:v0 . 
 ```

Then we build our spark batch image

```bash
$ cd batch/spark/ 
$ docker build -t  batch-spark:v0 .
```
## running k8s files
### first let's create a namespace that we will use for all our work
```bash
$ kubectl create ns "development"
```

### creating mongo-service
```bash
$ kubectl create -f mongo-service.yaml
```
### creating mongo rc
```bash
$ kubectl create -f mongo-controller.yaml
```

### creating batchspark job rc
```bash
$ kubectl create -f batch-spark-job.yaml
```

### creating webapp deployment
```bash
$ kubectl create -f webapp-deployment.yaml
```
### create webapp service
```bash
$ kubectl expose deployment webapp-deployment --type="LoadBalancer" --namespace="development"
```

### get the ip to access the application:
```bash
$ minikube service webapp-deployment --url --namespace="development"
```

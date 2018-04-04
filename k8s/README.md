# Deploying the application on Kubernetes

Note : We are currently working on this file ...

We are going to use kubernetes to deploy our application. Why kubernetes? 
We will answer this question in the following days ...
For more information about Kubernetes please visite the official doc : https://kubernetes.io/

## Setup environment
We are going to need to install:

- Docker (https://docs.docker.com/docker-for-windows/)
- VirtualBox (https://www.virtualbox.org/wiki/Downloads)
- Minikube (https://github.com/kubernetes/minikube)
- Kubectl (here I’m using version 1.9.0) (https://kubernetes.io/docs/tasks/tools/install-kubectl/)

### Reusing the Docker daemon
To be able to work with the docker daemon on your mac/linux host use the docker-env command in your shell:
more information can be foun here : https://kubernetes.io/docs/getting-started-guides/minikube/

```bash
$ minikube docker-env
 ```
 you will have something like this : 

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

## Running k8s files

### first let's create a namespace that we will use for all our work
```bash
$ cd k8s/
$ kubectl create ns "development"
namespace "development" created
```

### creating a persistent volume
```bash
$ kubectl create -f persistent-volume.yaml
persistentvolume "block-pv" created
```

### creating a persistent volume claim
```bash
$ kubectl create -f mongo-pvc.yaml
persistentvolumeclaim "mongo-pvc" created
```

### creating mongo-service
```bash
$ kubectl create -f mongo-service.yaml
service "mongo" created
```

### creating mongo rc
```bash
$ kubectl create -f mongo-controller.yaml
replicationcontroller "mongo-controller" created
```

### creating batchspark job rc
```bash
$ kubectl create -f batch-spark-job.yaml
job "batch-spark-job" created
```

### creating webapp deployment
```bash
$ kubectl create -f webapp-deployment.yaml
deployment "webapp-deployment" created
```

### create webapp service
```bash
$ kubectl expose deployment webapp-deployment --type="LoadBalancer" --namespace="development"
service "webapp-deployment" exposed
```

### get the ip to access the application:
```bash
$ minikube service webapp-deployment --url --namespace="development"
http://192.168.99.100:31451
```
use this url 'http://192.168.99.100:31451' to access the application (the port will change, so make sure you replace it with the one you get)

## Clean up
Now you can clean up the resources you created in your cluster:

```bash
$ kubectl delete service webapp-deployment --namespace="development"
$ kubectl delete service mongo --namespace="development"
$ kubectl delete rc mongo-controller --namespace="development"
$ kubectl delete job batch-spark-job --namespace="development"
$ kubectl delete deployment webapp-deployment --namespace="development"
$ kubectl delete pvc  mongo-pvc --namespace="development"
$ kubectl delete pv block-pv --namespace="development"
$ kubectl delete ns development
```
Optionally, stop the Minikube VM:

```bash
$ minikube stop
$ eval $(minikube docker-env -u)
```
Optionally, delete the Minikube VM:

```bash
$ minikube delete
```
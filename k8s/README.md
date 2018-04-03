# Deploying the application on Kubernetes

Note : We are currently working on this file ...

WE are going to use kubernetes to deploy our application. Why kubernetes? Now you are asking the real questions ...


## Build the needed images
First we are going to build the images we want to deploy. For the moment, we are only going to deploy the webapp aplication (angular/nodeJS), with the mongoDB (the webapp needs it to run)
and the spark bathc that will inject the list of airports in mongoDB after it starts.

Let's start ...

```bash
cd webapp/ 
docker build -t webapp:v0 . 
 ```

```bash
cd batch/spark/ 
docker build -t  batch-spark:v0 .
```

## first let's create a namespace that we will use for all our work
```bash
kubectl create ns "development"
```

## creating mongo-service
```bash
kubectl create -f mongo-service.yaml
```
## creating mongo rc
```bash
kubectl create -f mongo-controller.yaml
```

## creating batchspark job rc
```bash
kubectl create -f batch-spark-job.yaml
```

## creating webapp deployment
```bash
kubectl create -f webapp-deployment.yaml
```
## create webapp service
```bash
kubectl expose deployment webapp-deployment --type="LoadBalancer" --namespace="development"
```

## get the ip to access the application:
```bash
minikube service webapp-deployment --url --namespace="development"
```

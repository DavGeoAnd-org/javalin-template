# javalin-template

## Create any necessary steps

## VPC

* home_project-vpc

## Security Group - for services load balancer

* Create
    * Security group name: home_project-ecs-service-load_balancer-security_group
    * Description: Security group for ecs services load balancer running in home project
    * VPC: home_project-vpc
    * Inbound rules:
        * Type: HTTP -- Source: Anywhere-IPv4
        * Type: HTTP -- Source: Anywhere-IPv6

## Security Group - for services

* Create
    * Security group name: home_project-ecs-service-security_group
    * Description: Security group for ecs services running in home project
    * VPC: home_project-vpc
    * Inbound rules:
        * Type: Custom TCP -- Port range: 8080 -- Source: My IP
        * Type: HTTP -- Source: Custom -> Security group ID of 'Security Group - for services load balancer'

## Security Group - for otel-collector

* Create
    * Security group name: home_project-ecs-otel_collector-security_group
    * Description: Security group for ecs otel-collector running in home project
    * VPC: home_project-vpc
    * Inbound rules:
        * Type: Custom TCP -- Port range: 4318 -- Source: Anywhere-IPv4
        * Type: Custom TCP -- Port range: 4318 -- Source: Anywhere-IPv6
        * Type: Custom TCP -- Port range: 13133 -- Source: Anywhere-IPv4
        * Type: Custom TCP -- Port range: 13133 -- Source: Anywhere-IPv6

## Target Group - default target group for home_project

* Create
    * Choose a target type: IP addresses
    * Target group name: homeproject-default-tg
    * Protocol : Port: HTTP: 80
    * VPC: home_project-vpc
    * Protocol version: HTTP1
    * Network: home_project-vpc
    * Enter an IPv4 address from a VPC subnet.: Remove

## Application Load Balancer

* Create
    * Load balancer name: homeproject-services
    * Scheme: Internet-facing
    * Load balancer IP address type: IPv4
    * VPC: home_project-vpc
    * Mappings: all availability zones (public)
    * Security groups: home_project-ecs-service-load_balancer-security_group
    * Default action: homeproject-default-tg

## S3

* Create
    * Bucket type: General purpose
    * Bucket name: homeproject-services-bucket-396607284401
    * Create folder -> Folder name: javalin-template
    * In javalin-template folder
        * Create folder -> Folder name: [test|prod]
        * add .env files

## ECS Cluster

* Create
    * Cluster name: home_project-services-[test|prod]-cluster
    * Default namespace: home_project-[test|prod]-namespace
    * Infrastructure: AWS Fargate (serverless)

## ECS Task Definition - javalin-template

* Create
    * Task definition family: javalin-template-[test|prod]
    * Launch type: AWS Fargate
    * Task size:
        * CPU: .25 -- Memory: .5
    * Container - 1:
        * Name: javalin-template
        * Image URI: latest version
        * Port mappings:
            * Container port: 8080 -- Protocol: TCP -- App protocol: HTTP
        * Environment variables:
            * Add from file: .env files from homeproject-services-bucket-396607284401
        * Log collection: disable log collection

## ECS Service - javalin-template

* Create
    * Deploy from ecs task definition
    * Existing cluster: home_project-services-[test|prod]-cluster
    * Compute options: Capacity provider strategy
    * Service name: javalin-template
    * Desired tasks: 0
    * Service Connect:
        * Enable Use Service Connect
        * Service Connect configuration: Client side only
        * Namespace: home_project-[test|prod]-namespace
        * Disable Use log collection
    * Networking:
        * VPC: home_project-vpc
        * Subnets: enable only public
        * Security group: home_project-ecs-service-security_group
    * Load balancing (prod)
        * Enable Use load balancing
        * Load balancer type: Application Load Balancer
        * Application Load Balancer: Use an existing load balancer -> homeproject-services
        * Listener: Use an existing listener -> 80:HTTP
        * Target group:
            * Create new target group
            * Target group name: hp-javalin-template-http-tg
            * Path pattern: /template/*
            * Evaluation order: 1 (or next available)
            * Health check path: /template/admin/health

## ECS Task Definition - otel-collector-service

* Create
    * Task definition family: otel-collector-service-[test|prod]
    * Launch type: AWS Fargate
    * Task size:
        * CPU: .25 -- Memory: .5
    * Container - 1:
        * Name: otel-collector-service
        * Image URI: latest version
        * Port mappings:
            * Container port: 4317 -- Protocol: TCP -- App protocol: GRPC
            * Container port: 4318 -- Protocol: TCP -- App protocol: HTTP
            * Container port: 13133 -- Protocol: TCP -- App protocol: HTTP
            * Container port: 55678 -- Protocol: TCP -- App protocol: HTTP
            * Container port: 55679 -- Protocol: TCP -- App protocol: HTTP
        * Log collection: disable log collection

## ECS Service - otel-collector-service

* Create
    * Deploy from ecs task definition
    * Existing cluster: home_project-services[-test|-prod]-cluster
    * Compute options: Capacity provider strategy
    * Service name: otel-collector-service
    * Desired tasks: 0
    * Service Connect:
        * Enable Use Service Connect
        * Service Connect configuration: Client and server
        * Namespace: home_project[-test|-prod]-namespace
        * Service Connect and discovery name configuration:
            * Port alias: otel-collector-service-4318-tcp -- Discovery: http_otel-collector-service_4318 -- DNS:
              otel-collector-service -- Port: 4318
        * Disable Use log collection
    * Networking:
        * VPC: home_project-vpc
        * Subnets: enable only public
        * Security group: home_project-ecs-otel_collector-security_group
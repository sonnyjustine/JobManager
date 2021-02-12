# Job Management using Activiti with Spring Boot

This is a Spring Boot application for Job entities which supports changing of Job States using the Activiti BPM engine, JPA and REST

## The Job entity
Job Entity should support the following properties:
```json
{
    "id": "string or number",
    "type": "TypeA or TypeB",
    "state": #{"Unallocated", "Allocated", "StateA", "StateB", "Completed",
"Deleted"} // only possible one at any time
}
```

## The Job workflow
This is a simulation of a Job management system offering 2 different flows: one for TypeA jobs and another one for TypeB jobs
TypeA job instances support the following sequential states
```
Unallocated ---> Allocated ---> StateA ---> StateB ---> Completed
```
But you can't transit to any state bypass the previous one, for example, you can't transit job state from
Allocated to Completed, only could from StateB to Completed.
TypeB job instances would do the same but also support StateB ---> StateA as well.
We could change job state to Deleted at any state.

## Activiti
This application uses Activiti engine to support the Job workflow above. Each Job States are represented by a UserTask. Once a **UserTask** is completed, it will automatically transition to the _updateJobState_ **ServiceTask**.
The transitioning back to and from StateA is supported by IntermediateThrowEvents & BoundaryEvents.

See JobManagementProcess.bpm. You may upload or open this to bpm file readers to show the workflow diagram.

# The REST-ful API API
The REST API exposes endpoints for receiving events to trigger the transitions for the Activiti workflow.
The API is documented using Swagger OpenAPI. You may access it in /swagger-ui.html
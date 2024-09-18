import http from 'k6/http'
import {check, sleep} from 'k6'
import exec from 'k6/execution'
import {SharedArray} from 'k6/data';

const carChecklistsCount = 10
const tasksPerChecklistCount = 20

export const options = {
  scenarios: {
    default: {
      executor: 'constant-vus',
      vus: 2,
      duration: '15s',
    }
  }
}

export default function () {
  const taskInfo = getNextTask()
  markTaskCompleted(taskInfo)
  sleep(1)
}

function getNextTask() {
  const iteration = exec.scenario.iterationInTest

  const listIndex = Math.floor(iteration / tasksPerChecklistCount)
  const carChecklistId = carChecklistIds[listIndex]
  const taskIndex = iteration % tasksPerChecklistCount

  return {
    carChecklistId,
    taskIndex,
  }
}

const carChecklistIds = new SharedArray(
  'car-checklist-ids',
  function () {
    const uuidList = [];
    for (let i = 0; i < carChecklistsCount; i++) {
      uuidList.push(generateUUID())
    }
    return uuidList;
  }
)

function generateUUID() {
  return 'xxxxxxxx-xxxx-4xxx-yxxx-xxxxxxxxxxxx'.replace(/[xy]/g, function (c) {
    const r = (Math.random() * 16) | 0,
      v = c === 'x' ? r : (r & 0x3 | 0x8);
    return v.toString(16);
  })
}

function markTaskCompleted(taskInfo) {
  
  const isFirstTask = taskInfo.taskIndex === 0
  
  if (isFirstTask) {
    startAssemblingCar(taskInfo.carChecklistId)
  }
  
  const requestBody = {
    completedBy: 'santi'
  }

  const res = http.put(
    `http://localhost:8080/api/v1/assembly/${taskInfo.carChecklistId}/tasks/${taskInfo.taskIndex}/complete`,
    JSON.stringify(requestBody),
    {headers: {'Content-Type': 'application/json'}}
  )
  
  console.log(`Result Code: [${res.status}] VU: [${exec.vu.idInTest}], Iteration: [${exec.scenario.iterationInInstance}]`)
  
  check(res, {'status was 200': r => r.status === 200})
}

function startAssemblingCar(carChecklistId) {
  const requestBody = {
    carChecklistId,
    carModel: {
      make: 'some make',
      trim: 'some trim',
    },
    customizations: [
      {
        description: 'some description'
      },
      {
        description: 'some other description'
      }
    ]
  }

  const res = http.post(
    'http://localhost:8080/api/v1/assembly/start',
    JSON.stringify(requestBody),
    {headers: {'Content-Type': 'application/json'}}
  )

  check(res, {'status was 201': r => r.status === 201})
}

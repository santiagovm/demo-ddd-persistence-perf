import {check, sleep} from 'k6'
import {SharedArray} from 'k6/data'
import exec from 'k6/execution'
import http from 'k6/http'
import {uuidv4} from 'https://jslib.k6.io/k6-utils/1.4.0/index.js'

const carChecklistsCount = 2000
const tasksPerChecklistCount = 750

export const options = {
  scenarios: {
    default: {
      executor: 'ramping-vus',
      startVUs: 1,
      stages: [
        {target: 20, duration: '15s'},
        {target: 20, duration: '15s'},
      ],
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
    const uuidList = []
    for (let i = 0; i < carChecklistsCount; i++) {
      uuidList.push(uuidv4())
    }
    return uuidList
  }
)

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

  // console.log(`Complete Result Code1: [${res.status}] VU: [${exec.vu.idInTest}], Iteration: [${exec.scenario.iterationInInstance}]`)

  const checklistNotFound = res.status === 404

  if (checklistNotFound) {
    startAssemblingCar(taskInfo.carChecklistId)

    const res2 = http.put(
      `http://localhost:8080/api/v1/assembly/${taskInfo.carChecklistId}/tasks/${taskInfo.taskIndex}/complete`,
      JSON.stringify(requestBody),
      {headers: {'Content-Type': 'application/json'}}
    )

    // console.log(`Complete Result Code2: [${res2.status}] VU: [${exec.vu.idInTest}], Iteration: [${exec.scenario.iterationInInstance}]`)

    check(res2, {'status was 200': r => r.status === 200})
  } else {
    check(res, {'status was 200': r => r.status === 200})
  }
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

  // console.log(`Start Result Code: [${res.status}] VU: [${exec.vu.idInTest}], Iteration: [${exec.scenario.iterationInInstance}]`)

  check(res, {'status was 201': r => r.status === 201})
}

import http from 'k6/http'
import {check} from 'k6'

export const options = {
  scenarios: {
    default: {
      executor: 'constant-vus',
      vus: 16,
      duration: '1m',
      gracefulStop: '3s',
    }
  }
}

export default function () {
  const carId = startAssemblingCar()
  markAllTasksCompleted(carId)
}

function markTaskCompleted(carId, taskIndex) {
  const requestBody = {
    completedBy: 'santi'
  }

  const res = http.put(
    `http://localhost:8080/api/v1/assembly/${carId}/tasks/${taskIndex}/complete`,
    JSON.stringify(requestBody),
    {headers: {'Content-Type': 'application/json'}}
  )

  check(res, {'status was 200': r => r.status === 200})
}


function markAllTasksCompleted(carId) {
  for (let i = 0; i < 1000; i++) {
    markTaskCompleted(carId, i)
  }
}

function startAssemblingCar() {
  const requestBody = {
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

  const location = res.headers.Location
  return location.substring("http://localhost:8080/api/v1/assembly/".length)
}

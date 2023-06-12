import { react } from '@/utils/request'

export function getDashboardData(props = [], headers = {}) {
  return react({
    headers,
    url: '/dashboard/_multi',
    method: 'post',
    data: props
  })
}

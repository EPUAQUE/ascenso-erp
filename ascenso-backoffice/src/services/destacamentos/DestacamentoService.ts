import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { Destacamento } from '@/types/destacamentos'

class DestacamentoService {
  listar() {
    return apiClient.get<Destacamento[]>(API_ENDPOINTS.destacamentos.base)
  }
}

export const destacamentoService = new DestacamentoService()

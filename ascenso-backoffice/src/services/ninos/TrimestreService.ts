import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { GuardarTrimestreRequest, Trimestre } from '@/types/ninos'

class TrimestreService {
  listar() {
    return apiClient.get<Trimestre[]>(API_ENDPOINTS.trimestres.base)
  }

  crear(request: GuardarTrimestreRequest) {
    return apiClient.post<Trimestre>(API_ENDPOINTS.trimestres.base, request)
  }

  actualizar(id: number, request: GuardarTrimestreRequest) {
    return apiClient.put<Trimestre>(API_ENDPOINTS.trimestres.porId(id), request)
  }
}

export const trimestreService = new TrimestreService()

import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { MedallaOtorgada, OtorgarMedallaRequest } from '@/types/ninos'

class MedallaOtorgadaService {
  listar(destacamentoId: number, ninoId: number) {
    return apiClient.get<MedallaOtorgada[]>(API_ENDPOINTS.medallas.porNino(destacamentoId, ninoId))
  }

  otorgar(destacamentoId: number, ninoId: number, request: OtorgarMedallaRequest) {
    return apiClient.post<MedallaOtorgada>(API_ENDPOINTS.medallas.porNino(destacamentoId, ninoId), request)
  }
}

export const medallaOtorgadaService = new MedallaOtorgadaService()

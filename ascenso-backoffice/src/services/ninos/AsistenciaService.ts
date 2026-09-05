import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { Asistencia, CorregirAsistenciaRequest, RegistrarAsistenciaRequest } from '@/types/ninos'

class AsistenciaService {
  listar(destacamentoId: number, ninoId: number) {
    return apiClient.get<Asistencia[]>(API_ENDPOINTS.asistencia.porNino(destacamentoId, ninoId))
  }

  registrar(destacamentoId: number, ninoId: number, request: RegistrarAsistenciaRequest) {
    return apiClient.post<Asistencia>(API_ENDPOINTS.asistencia.porNino(destacamentoId, ninoId), request)
  }

  corregir(destacamentoId: number, ninoId: number, id: number, request: CorregirAsistenciaRequest) {
    return apiClient.put<Asistencia>(API_ENDPOINTS.asistencia.porId(destacamentoId, ninoId, id), request)
  }
}

export const asistenciaService = new AsistenciaService()

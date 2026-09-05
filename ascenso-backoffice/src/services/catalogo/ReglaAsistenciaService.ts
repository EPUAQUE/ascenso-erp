import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { GuardarReglaAsistenciaRequest, ReglaAsistencia } from '@/types/catalogo'

class ReglaAsistenciaService {
  listar() {
    return apiClient.get<ReglaAsistencia[]>(API_ENDPOINTS.catalogo.reglasAsistencia)
  }

  obtenerVigente() {
    return apiClient.get<ReglaAsistencia>(API_ENDPOINTS.catalogo.reglaAsistenciaVigente)
  }

  crear(request: GuardarReglaAsistenciaRequest) {
    return apiClient.post<ReglaAsistencia>(API_ENDPOINTS.catalogo.reglasAsistencia, request)
  }

  actualizar(id: number, request: GuardarReglaAsistenciaRequest) {
    return apiClient.put<ReglaAsistencia>(API_ENDPOINTS.catalogo.reglaAsistenciaPorId(id), request)
  }
}

export const reglaAsistenciaService = new ReglaAsistenciaService()

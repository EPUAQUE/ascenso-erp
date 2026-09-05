import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { GuardarLibroBiblicoRequest, LibroBiblico } from '@/types/catalogo'

class LibroBiblicoService {
  listar(grupoId?: number) {
    return apiClient.get<LibroBiblico[]>(API_ENDPOINTS.catalogo.librosBiblicos, {
      params: grupoId ? { grupoId } : undefined,
    })
  }

  crear(request: GuardarLibroBiblicoRequest) {
    return apiClient.post<LibroBiblico>(API_ENDPOINTS.catalogo.librosBiblicos, request)
  }

  actualizar(id: number, request: GuardarLibroBiblicoRequest) {
    return apiClient.put<LibroBiblico>(API_ENDPOINTS.catalogo.libroBiblicoPorId(id), request)
  }

  activar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.catalogo.libroBiblicoActivar(id))
  }

  desactivar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.catalogo.libroBiblicoDesactivar(id))
  }
}

export const libroBiblicoService = new LibroBiblicoService()

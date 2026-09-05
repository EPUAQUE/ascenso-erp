import { apiClient } from '@/services/http/ApiClient'
import { API_ENDPOINTS } from '@/config/endpoints'
import type { Anuncio, GuardarAnuncioRequest } from '@/types/actividades'

class AnuncioService {
  listar() {
    return apiClient.get<Anuncio[]>(API_ENDPOINTS.anuncios.base)
  }

  crear(request: GuardarAnuncioRequest) {
    return apiClient.post<Anuncio>(API_ENDPOINTS.anuncios.base, request)
  }

  actualizar(id: number, request: GuardarAnuncioRequest) {
    return apiClient.put<Anuncio>(API_ENDPOINTS.anuncios.porId(id), request)
  }

  activar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.anuncios.activar(id))
  }

  desactivar(id: number) {
    return apiClient.post<void>(API_ENDPOINTS.anuncios.desactivar(id))
  }
}

export const anuncioService = new AnuncioService()

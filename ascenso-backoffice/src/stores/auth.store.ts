import { defineStore } from 'pinia'
import { authService } from '@/services/auth/AuthService'
import { refreshAccessToken } from '@/services/http/ApiClient'
import { tokenService } from '@/services/http/token.service'
import { usePermissionsStore } from './permissions.store'
import { useUserStore } from './user.store'
import { useDestacamentoStore } from './destacamento.store'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    authorizationLoaded: false,
  }),
  actions: {
    async login(username: string, password: string) {
      const response = await authService.login(username, password)
      tokenService.set(response.accessToken)
      await this.loadAuthorization()
    },

    async loadAuthorization() {
      const me = await authService.me()
      useUserStore().setUsername(me.username)
      usePermissionsStore().hydrate(me.permisos, me.destacamentoIds, me.alcanceGlobal)
      this.authorizationLoaded = true
    },

    /**
     * El access token vive solo en memoria (ver token.service.ts) — se pierde en
     * cada recarga de página. Este intento de refresh silencioso, corrido por el
     * guard de rutas antes de redirigir a /login, aprovecha la cookie HttpOnly de
     * refresh token para restaurar la sesión sin pedir credenciales de nuevo.
     */
    async trySilentLogin(): Promise<boolean> {
      try {
        await refreshAccessToken()
        await this.loadAuthorization()
        return true
      } catch {
        tokenService.clear()
        return false
      }
    },

    async logout() {
      try {
        await authService.logout()
      } finally {
        tokenService.clear()
        usePermissionsStore().clear()
        useUserStore().clear()
        useDestacamentoStore().clear()
        this.authorizationLoaded = false
      }
    },
  },
})

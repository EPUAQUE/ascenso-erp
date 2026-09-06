<script setup lang="ts">
import { computed, ref } from 'vue'
import { useRoute, useRouter } from 'vue-router'
import { useAuthStore } from '@/stores/auth.store'
import { useUserStore } from '@/stores/user.store'
import { usePermissionsStore } from '@/stores/permissions.store'
import { useThemeStore } from '@/stores/theme.store'
import NavIcon from '@/components/common/NavIcon.vue'

interface NavItem {
  label: string
  path: string
  icon: string
}

interface NavGroup {
  label: string
  items: NavItem[]
}

const router = useRouter()
const route = useRoute()
const authStore = useAuthStore()
const userStore = useUserStore()
const permissions = usePermissionsStore()
const theme = useThemeStore()

// Fase 1 (scaffold): sin permisos por módulo todavía — cada módulo real de
// niños/catálogo/asistencia/actividades definirá su propio permiso al
// construirse; por ahora todos los items son visibles a cualquier usuario
// autenticado (ver routes.ts, requiresAuth: true sin `permission`).
const navGroups: NavGroup[] = [
  {
    label: 'Programa',
    items: [
      { label: 'Niños', path: '/ninos', icon: 'ninos' },
      { label: 'Grupos', path: '/grupos', icon: 'grupos' },
      { label: 'Reglas de asistencia', path: '/reglas-asistencia', icon: 'reglas' },
      { label: 'Asistencia', path: '/asistencia', icon: 'asistencia' },
      { label: 'Trimestres', path: '/trimestres', icon: 'trimestres' },
      { label: 'Actividades', path: '/actividades', icon: 'actividades' },
      { label: 'Anuncios', path: '/anuncios', icon: 'anuncios' },
    ],
  },
]

const visibleGroups = computed(() => navGroups)

const destacamentoLabel = computed(() => {
  if (permissions.alcanceGlobal) return 'Todas las destacamentos'
  const n = permissions.destacamentoIds.size
  return n === 1 ? '1 destacamento asignado' : `${n} destacamento(s) asignado(s)`
})

const moduleTitle = computed(() => (route.meta.title as string | undefined) ?? 'Exploradores del Rey')

const iniciales = computed(() => (userStore.username ?? '??').slice(0, 2).toUpperCase())

const SIDEBAR_OCULTO_KEY = 'exploradores-sidebar-oculto'
const sidebarOculto = ref(localStorage.getItem(SIDEBAR_OCULTO_KEY) === '1')

function alternarSidebar() {
  sidebarOculto.value = !sidebarOculto.value
  localStorage.setItem(SIDEBAR_OCULTO_KEY, sidebarOculto.value ? '1' : '0')
}

const searchOpen = ref(false)
const searchQuery = ref('')

const allNavItems = computed(() => navGroups.flatMap((g) => g.items))

const searchResults = computed(() => {
  const q = searchQuery.value.trim().toLowerCase()
  if (!q) return []
  return allNavItems.value.filter((item) => item.label.toLowerCase().includes(q)).slice(0, 8)
})

function goToResult(item: NavItem) {
  router.push(item.path)
  searchQuery.value = ''
  searchOpen.value = false
}

function onSearchBlur() {
  window.setTimeout(() => {
    searchOpen.value = false
  }, 150)
}

async function onLogout() {
  // authStore.logout() relanza si el POST a /auth/logout falla — el estado
  // local ya quedó limpio en ese caso, así que igual hay que redirigir a
  // /login en vez de dejar al usuario varado.
  try {
    await authStore.logout()
  } finally {
    router.push({ name: 'login' })
  }
}
</script>

<template>
  <div class="flex min-h-screen bg-mk-bg text-mk-text">
    <aside
      class="mk-sidebar flex shrink-0 flex-col overflow-hidden text-mk-brand-ink transition-[width] duration-200"
      :class="sidebarOculto ? 'w-0' : 'w-64'"
    >
      <div class="flex items-center gap-2 px-5 py-5">
        <div
          class="flex h-9 items-center justify-center rounded-md bg-white/10 px-2 text-xs font-bold tracking-wide"
        >
          EdR
        </div>
        <div class="leading-tight">
          <p class="text-sm font-bold tracking-wide">Exploradores del Rey</p>
          <p class="text-[11px] text-white/60">Backoffice del programa</p>
        </div>
      </div>

      <div class="mx-4 mb-4 rounded-md bg-white/5 px-3 py-2">
        <p class="text-[10px] font-semibold uppercase tracking-wider text-white/50">Destacamento</p>
        <p class="truncate text-sm font-medium">{{ destacamentoLabel }}</p>
      </div>

      <nav class="flex-1 space-y-5 overflow-y-auto px-3 pb-4">
        <div v-for="group in visibleGroups" :key="group.label">
          <p class="px-2 pb-1.5 text-[11px] font-semibold uppercase tracking-wider text-white/45">
            {{ group.label }}
          </p>
          <RouterLink
            v-for="item in group.items"
            :key="item.path"
            :to="item.path"
            class="group flex items-center gap-2.5 rounded-md px-2.5 py-2 text-sm font-medium text-white/75 shadow-none transition-colors hover:bg-white/10 hover:text-white"
            active-class="!bg-mk-nav-active !text-mk-nav-active-ink shadow-lg shadow-mk-nav-active/40"
          >
            <NavIcon
              :name="item.icon"
              class="h-4 w-4 shrink-0 opacity-80 group-[.router-link-active]:opacity-100"
            />
            {{ item.label }}
          </RouterLink>
        </div>
      </nav>
    </aside>

    <div class="flex min-h-screen flex-1 flex-col">
      <header
        class="flex items-center justify-between gap-4 border-b border-mk-border bg-mk-surface px-6 py-3"
      >
        <div class="flex min-w-0 items-center gap-3">
          <button
            type="button"
            class="mk-btn-ghost flex h-9 w-9 shrink-0 items-center justify-center rounded-md"
            :title="sidebarOculto ? 'Mostrar menú' : 'Ocultar menú'"
            @click="alternarSidebar"
          >
            <svg class="h-5 w-5" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2">
              <path d="M4 6h16M4 12h16M4 18h16" stroke-linecap="round" />
            </svg>
          </button>
          <div class="min-w-0">
            <h1 class="truncate text-base font-bold text-mk-text">{{ moduleTitle }}</h1>
            <p class="truncate text-xs text-mk-text-muted">Exploradores del Rey / {{ moduleTitle }}</p>
          </div>
        </div>

        <div class="relative w-full max-w-sm">
          <svg
            class="pointer-events-none absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-mk-text-muted"
            viewBox="0 0 24 24"
            fill="none"
            stroke="currentColor"
            stroke-width="2"
          >
            <circle cx="11" cy="11" r="7" />
            <path d="m21 21-4.3-4.3" stroke-linecap="round" />
          </svg>
          <input
            v-model="searchQuery"
            type="text"
            placeholder="Buscar un módulo…"
            class="mk-input w-full rounded-md border border-mk-border py-2 pl-9 pr-3 text-sm"
            @focus="searchOpen = true"
            @blur="onSearchBlur"
          />
          <div
            v-if="searchOpen && searchResults.length > 0"
            class="mk-card absolute z-10 mt-1 w-full overflow-hidden py-1"
          >
            <button
              v-for="item in searchResults"
              :key="item.path"
              type="button"
              class="block w-full px-3 py-1.5 text-left text-sm hover:bg-mk-surface-2"
              @click="goToResult(item)"
            >
              {{ item.label }}
            </button>
          </div>
        </div>

        <div class="flex items-center gap-2">
          <button
            type="button"
            class="mk-btn-ghost flex h-9 w-9 items-center justify-center rounded-md"
            :title="theme.tema === 'oscuro' ? 'Cambiar a modo claro' : 'Cambiar a modo oscuro'"
            @click="theme.alternar()"
          >
            <svg
              v-if="theme.tema === 'oscuro'"
              class="h-5 w-5"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <circle cx="12" cy="12" r="4" />
              <path
                d="M12 2v2M12 20v2M4.9 4.9l1.4 1.4M17.7 17.7l1.4 1.4M2 12h2M20 12h2M4.9 19.1l1.4-1.4M17.7 6.3l1.4-1.4"
                stroke-linecap="round"
              />
            </svg>
            <svg
              v-else
              class="h-5 w-5"
              viewBox="0 0 24 24"
              fill="none"
              stroke="currentColor"
              stroke-width="2"
            >
              <path
                d="M21 12.8A9 9 0 1 1 11.2 3 7 7 0 0 0 21 12.8Z"
                stroke-linecap="round"
                stroke-linejoin="round"
              />
            </svg>
          </button>

          <div class="mx-1 h-6 w-px bg-mk-border" />

          <div
            class="flex h-8 w-8 shrink-0 items-center justify-center rounded-full bg-mk-accent text-xs font-extrabold text-mk-brand"
            :title="userStore.username ?? ''"
          >
            {{ iniciales }}
          </div>
          <span class="hidden text-sm text-mk-text-muted sm:inline">{{ userStore.username }}</span>
          <button type="button" class="mk-btn mk-btn-ghost px-3 py-1.5 text-sm" @click="onLogout">
            Salir
          </button>
        </div>
      </header>

      <main class="flex-1">
        <RouterView />
      </main>
    </div>
  </div>
</template>

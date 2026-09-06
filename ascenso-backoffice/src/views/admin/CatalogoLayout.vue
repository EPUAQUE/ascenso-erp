<script setup lang="ts">
import { computed } from 'vue'
import { useRoute } from 'vue-router'

const route = useRoute()

// "Grupos" debe seguir resaltado mientras se navega dentro de un grupo
// (/catalogo/grupos/:grupoId/...) — esas rutas son hermanas de
// 'catalogo-grupos', no descendientes, así que el active-class automático
// de RouterLink (que compara ancestro/descendiente) no las cubre.
const enGrupos = computed(() => route.path.startsWith('/catalogo/grupos'))
</script>

<template>
  <div class="space-y-4 p-6">
    <div class="mk-card overflow-x-auto">
      <nav class="mk-scroll-x flex gap-1 p-1.5">
        <RouterLink
          :to="{ name: 'catalogo-grupos' }"
          class="shrink-0 rounded-md px-3 py-1.5 text-sm font-medium transition-colors"
          :class="
            enGrupos
              ? '!bg-mk-nav-active !text-mk-nav-active-ink'
              : 'text-mk-text-muted hover:bg-mk-surface-2 hover:text-mk-text'
          "
        >
          Grupos
        </RouterLink>
        <RouterLink
          :to="{ name: 'catalogo-reglas' }"
          class="shrink-0 rounded-md px-3 py-1.5 text-sm font-medium text-mk-text-muted transition-colors hover:bg-mk-surface-2 hover:text-mk-text"
          active-class="!bg-mk-nav-active !text-mk-nav-active-ink"
        >
          Reglas de asistencia
        </RouterLink>
      </nav>
    </div>

    <RouterView />
  </div>
</template>

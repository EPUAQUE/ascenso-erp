<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { libroBiblicoService } from '@/services/catalogo/LibroBiblicoService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Grupo, LibroBiblico } from '@/types/catalogo'

const props = defineProps<{ grupos: Grupo[] }>()

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('CATALOGO_EDITAR'))

const libros = ref<LibroBiblico[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)
const filtroGrupoId = ref<number | null>(null)

function nombreGrupo(id: number): string {
  return props.grupos.find((g) => g.id === id)?.nombre ?? `Grupo #${id}`
}

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    libros.value = await libroBiblicoService.listar(filtroGrupoId.value ?? undefined)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar los libros bíblicos.'
  } finally {
    cargando.value = false
  }
}

watch(filtroGrupoId, cargar)
cargar()

const formOpen = ref(false)
const libroEnEdicion = ref<LibroBiblico | null>(null)
const grupoId = ref<number | null>(null)
const titulo = ref('')
const ordenSugerido = ref<number | null>(null)
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  libroEnEdicion.value = null
  grupoId.value = filtroGrupoId.value ?? props.grupos[0]?.id ?? null
  titulo.value = ''
  ordenSugerido.value = null
  formError.value = null
  formOpen.value = true
}

function abrirEditar(l: LibroBiblico) {
  libroEnEdicion.value = l
  grupoId.value = l.grupoId
  titulo.value = l.titulo
  ordenSugerido.value = l.ordenSugerido
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !grupoId.value || !titulo.value.trim()) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request = { grupoId: grupoId.value, titulo: titulo.value.trim(), ordenSugerido: ordenSugerido.value }
  try {
    if (libroEnEdicion.value) {
      const actualizado = await libroBiblicoService.actualizar(libroEnEdicion.value.id, request)
      const idx = libros.value.findIndex((l) => l.id === actualizado.id)
      if (idx >= 0) libros.value[idx] = actualizado
    } else {
      const creado = await libroBiblicoService.crear(request)
      libros.value.unshift(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar el libro bíblico.'
  } finally {
    guardando.value = false
  }
}

async function onToggleActivo(l: LibroBiblico) {
  errorMessage.value = null
  try {
    if (l.activo) {
      await libroBiblicoService.desactivar(l.id)
    } else {
      await libroBiblicoService.activar(l.id)
    }
    l.activo = !l.activo
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cambiar el estado.'
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <label class="flex items-center gap-2 text-sm">
        <span class="text-mk-text-muted">Grupo</span>
        <select
          v-model.number="filtroGrupoId"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
        >
          <option :value="null">Todos</option>
          <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
        </select>
      </label>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo libro bíblico
      </button>
    </div>

    <p
      v-if="errorMessage"
      class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
      role="alert"
    >
      {{ errorMessage }}
    </p>

    <div class="mk-card mk-scroll-x overflow-x-auto">
      <table class="w-full text-left text-sm">
        <thead>
          <tr
            class="border-b border-mk-border text-xs font-semibold uppercase tracking-wider text-mk-text-muted"
          >
            <th class="px-4 py-3">Título</th>
            <th class="px-4 py-3">Orden sugerido</th>
            <th class="px-4 py-3">Grupo</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="libros.length === 0">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">
              No hay libros bíblicos para mostrar.
            </td>
          </tr>
          <tr v-for="l in libros" v-else :key="l.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ l.titulo }}</td>
            <td class="mk-num px-4 py-2.5 text-mk-text-muted">{{ l.ordenSugerido ?? '—' }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ nombreGrupo(l.grupoId) }}</td>
            <td class="px-4 py-2.5">
              <EstadoBadge
                :variant="l.activo ? 'success' : 'neutral'"
                :label="l.activo ? 'Activo' : 'Inactivo'"
              />
            </td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(l)">
                  <ActionIcon name="edit" />
                </button>
                <button
                  type="button"
                  class="mk-row-btn"
                  :class="l.activo ? 'mk-row-btn-danger' : 'mk-row-btn-success'"
                  :title="l.activo ? 'Desactivar' : 'Activar'"
                  @click="onToggleActivo(l)"
                >
                  <ActionIcon name="power" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" :title="libroEnEdicion ? 'Editar libro bíblico' : 'Nuevo libro bíblico'">
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Grupo</label>
          <select
            v-model.number="grupoId"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          >
            <option v-for="g in grupos" :key="g.id" :value="g.id">{{ g.nombre }}</option>
          </select>
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Título</label>
          <input
            v-model="titulo"
            type="text"
            maxlength="120"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Orden sugerido (opcional)</label>
          <input
            v-model.number="ordenSugerido"
            type="number"
            min="1"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div class="flex justify-end gap-2 pt-2">
          <button type="button" class="mk-btn mk-btn-ghost" @click="formOpen = false">Cancelar</button>
          <button type="submit" :disabled="guardando" class="mk-btn mk-btn-primary">
            {{ guardando ? 'Guardando…' : 'Guardar' }}
          </button>
        </div>
      </form>
    </ModalDialog>
  </div>
</template>

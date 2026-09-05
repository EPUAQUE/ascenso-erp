<script setup lang="ts">
import { computed, ref, watch } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { useDestacamentoStore } from '@/stores/destacamento.store'
import { usePermissionsStore } from '@/stores/permissions.store'
import { actividadService } from '@/services/actividades/ActividadService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Actividad } from '@/types/actividades'

const destacamentoStore = useDestacamentoStore()
const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('ACTIVIDADES_EDITAR'))

const actividades = ref<Actividad[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

function instantALocal(instant: string | null): string {
  if (!instant) return ''
  const d = new Date(instant)
  const pad = (n: number) => String(n).padStart(2, '0')
  return `${d.getFullYear()}-${pad(d.getMonth() + 1)}-${pad(d.getDate())}T${pad(d.getHours())}:${pad(d.getMinutes())}`
}

function localAInstant(value: string): string | null {
  return value ? new Date(value).toISOString() : null
}

function formatearFecha(instant: string | null): string {
  return instant ? new Date(instant).toLocaleString() : '—'
}

async function cargar() {
  const destacamentoId = destacamentoStore.actualId
  if (!destacamentoId) {
    actividades.value = []
    return
  }
  cargando.value = true
  errorMessage.value = null
  try {
    actividades.value = await actividadService.listar(destacamentoId)
  } catch (error) {
    errorMessage.value =
      error instanceof ApiClientError ? error.message : 'No se pudo cargar las actividades.'
  } finally {
    cargando.value = false
  }
}

watch(() => destacamentoStore.actualId, cargar, { immediate: true })

const formOpen = ref(false)
const actividadEnEdicion = ref<Actividad | null>(null)
const titulo = ref('')
const descripcion = ref('')
const fechaInicio = ref('')
const fechaFin = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  actividadEnEdicion.value = null
  titulo.value = ''
  descripcion.value = ''
  fechaInicio.value = ''
  fechaFin.value = ''
  formError.value = null
  formOpen.value = true
}

function abrirEditar(a: Actividad) {
  actividadEnEdicion.value = a
  titulo.value = a.titulo
  descripcion.value = a.descripcion ?? ''
  fechaInicio.value = instantALocal(a.fechaInicio)
  fechaFin.value = instantALocal(a.fechaFin)
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  const destacamentoId = destacamentoStore.actualId
  if (guardando.value || !destacamentoId || !titulo.value.trim() || !fechaInicio.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request = {
    titulo: titulo.value.trim(),
    descripcion: descripcion.value.trim() || null,
    fechaInicio: localAInstant(fechaInicio.value) as string,
    fechaFin: localAInstant(fechaFin.value),
  }
  try {
    if (actividadEnEdicion.value) {
      const actualizada = await actividadService.actualizar(
        destacamentoId,
        actividadEnEdicion.value.id,
        request,
      )
      const idx = actividades.value.findIndex((a) => a.id === actualizada.id)
      if (idx >= 0) actividades.value[idx] = actualizada
    } else {
      const creada = await actividadService.crear(destacamentoId, request)
      actividades.value.unshift(creada)
    }
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar la actividad.'
  } finally {
    guardando.value = false
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex flex-wrap items-center justify-between gap-3">
      <label v-if="destacamentoStore.opciones.length > 1" class="flex items-center gap-2 text-sm">
        <span class="text-mk-text-muted">Destacamento</span>
        <select
          :value="destacamentoStore.actualId ?? undefined"
          class="mk-input rounded-md border border-mk-border px-2 py-1.5 text-sm"
          @change="destacamentoStore.seleccionar(Number(($event.target as HTMLSelectElement).value))"
        >
          <option v-for="o in destacamentoStore.opciones" :key="o.id" :value="o.id">{{ o.nombre }}</option>
        </select>
      </label>
      <span v-else-if="destacamentoStore.opciones.length === 1" class="text-sm text-mk-text-muted">
        {{ destacamentoStore.opciones[0].nombre }}
      </span>

      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary ml-auto" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nueva actividad
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
            <th class="px-4 py-3">Descripción</th>
            <th class="px-4 py-3">Inicio</th>
            <th class="px-4 py-3">Fin</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="actividades.length === 0">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">
              No hay actividades para mostrar.
            </td>
          </tr>
          <tr v-for="a in actividades" v-else :key="a.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ a.titulo }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ a.descripcion ?? '—' }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ formatearFecha(a.fechaInicio) }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ formatearFecha(a.fechaFin) }}</td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(a)">
                  <ActionIcon name="edit" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" :title="actividadEnEdicion ? 'Editar actividad' : 'Nueva actividad'">
      <form class="space-y-4" @submit.prevent="onSubmit">
        <p
          v-if="formError"
          class="rounded-md bg-mk-danger/10 px-3 py-2 text-sm font-medium text-mk-danger"
          role="alert"
        >
          {{ formError }}
        </p>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Título</label>
          <input
            v-model="titulo"
            type="text"
            maxlength="150"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          />
        </div>

        <div>
          <label class="mb-1 block text-sm font-medium text-mk-text">Descripción (opcional)</label>
          <textarea
            v-model="descripcion"
            rows="3"
            class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
          ></textarea>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Inicio</label>
            <input
              v-model="fechaInicio"
              type="datetime-local"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Fin (opcional)</label>
            <input
              v-model="fechaFin"
              type="datetime-local"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
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

<script setup lang="ts">
import { computed, ref } from 'vue'
import ModalDialog from '@/components/common/ModalDialog.vue'
import EstadoBadge from '@/components/common/EstadoBadge.vue'
import ActionIcon from '@/components/common/ActionIcon.vue'
import { usePermissionsStore } from '@/stores/permissions.store'
import { anuncioService } from '@/services/actividades/AnuncioService'
import { ApiClientError } from '@/services/http/ApiClient'
import type { Anuncio } from '@/types/actividades'

const permissions = usePermissionsStore()
const puedeEditar = computed(() => permissions.can('ANUNCIOS_EDITAR'))

const anuncios = ref<Anuncio[]>([])
const cargando = ref(false)
const errorMessage = ref<string | null>(null)

async function cargar() {
  cargando.value = true
  errorMessage.value = null
  try {
    anuncios.value = await anuncioService.listar()
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cargar los anuncios.'
  } finally {
    cargando.value = false
  }
}

cargar()

const formOpen = ref(false)
const anuncioEnEdicion = ref<Anuncio | null>(null)
const titulo = ref('')
const descripcion = ref('')
const imagenUrl = ref('')
const enlaceUrl = ref('')
const fechaInicioVisible = ref('')
const fechaFinVisible = ref('')
const guardando = ref(false)
const formError = ref<string | null>(null)

function abrirCrear() {
  anuncioEnEdicion.value = null
  titulo.value = ''
  descripcion.value = ''
  imagenUrl.value = ''
  enlaceUrl.value = ''
  fechaInicioVisible.value = ''
  fechaFinVisible.value = ''
  formError.value = null
  formOpen.value = true
}

function abrirEditar(a: Anuncio) {
  anuncioEnEdicion.value = a
  titulo.value = a.titulo
  descripcion.value = a.descripcion ?? ''
  imagenUrl.value = a.imagenUrl ?? ''
  enlaceUrl.value = a.enlaceUrl ?? ''
  fechaInicioVisible.value = a.fechaInicioVisible
  fechaFinVisible.value = a.fechaFinVisible ?? ''
  formError.value = null
  formOpen.value = true
}

async function onSubmit() {
  if (guardando.value || !titulo.value.trim() || !fechaInicioVisible.value) {
    formError.value = 'Completa los campos requeridos.'
    return
  }
  guardando.value = true
  formError.value = null
  const request = {
    titulo: titulo.value.trim(),
    descripcion: descripcion.value.trim() || null,
    imagenUrl: imagenUrl.value.trim() || null,
    enlaceUrl: enlaceUrl.value.trim() || null,
    fechaInicioVisible: fechaInicioVisible.value,
    fechaFinVisible: fechaFinVisible.value || null,
  }
  try {
    if (anuncioEnEdicion.value) {
      const actualizado = await anuncioService.actualizar(anuncioEnEdicion.value.id, request)
      const idx = anuncios.value.findIndex((a) => a.id === actualizado.id)
      if (idx >= 0) anuncios.value[idx] = actualizado
    } else {
      const creado = await anuncioService.crear(request)
      anuncios.value.unshift(creado)
    }
    formOpen.value = false
  } catch (error) {
    formError.value = error instanceof ApiClientError ? error.message : 'No se pudo guardar el anuncio.'
  } finally {
    guardando.value = false
  }
}

async function onToggleActivo(a: Anuncio) {
  errorMessage.value = null
  try {
    if (a.activo) {
      await anuncioService.desactivar(a.id)
    } else {
      await anuncioService.activar(a.id)
    }
    a.activo = !a.activo
  } catch (error) {
    errorMessage.value = error instanceof ApiClientError ? error.message : 'No se pudo cambiar el estado.'
  }
}
</script>

<template>
  <div class="space-y-4">
    <div class="flex justify-end">
      <button v-if="puedeEditar" type="button" class="mk-btn mk-btn-primary" @click="abrirCrear">
        <ActionIcon name="plus" class="h-4 w-4" />
        Nuevo anuncio
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
            <th class="px-4 py-3">Visible desde</th>
            <th class="px-4 py-3">Visible hasta</th>
            <th class="px-4 py-3">Estado</th>
            <th class="px-4 py-3 text-right">Acciones</th>
          </tr>
        </thead>
        <tbody class="divide-y divide-mk-border">
          <tr v-if="cargando">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">Cargando…</td>
          </tr>
          <tr v-else-if="anuncios.length === 0">
            <td colspan="5" class="px-4 py-6 text-center text-mk-text-muted">
              No hay anuncios para mostrar.
            </td>
          </tr>
          <tr v-for="a in anuncios" v-else :key="a.id">
            <td class="px-4 py-2.5 font-medium text-mk-text">{{ a.titulo }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ a.fechaInicioVisible }}</td>
            <td class="px-4 py-2.5 text-mk-text-muted">{{ a.fechaFinVisible ?? '—' }}</td>
            <td class="px-4 py-2.5">
              <EstadoBadge
                :variant="a.activo ? 'success' : 'neutral'"
                :label="a.activo ? 'Activo' : 'Inactivo'"
              />
            </td>
            <td class="px-4 py-2.5">
              <div v-if="puedeEditar" class="mk-row-actions justify-end">
                <button type="button" class="mk-row-btn" title="Editar" @click="abrirEditar(a)">
                  <ActionIcon name="edit" />
                </button>
                <button
                  type="button"
                  class="mk-row-btn"
                  :class="a.activo ? 'mk-row-btn-danger' : 'mk-row-btn-success'"
                  :title="a.activo ? 'Desactivar' : 'Activar'"
                  @click="onToggleActivo(a)"
                >
                  <ActionIcon name="power" />
                </button>
              </div>
            </td>
          </tr>
        </tbody>
      </table>
    </div>

    <ModalDialog v-model="formOpen" :title="anuncioEnEdicion ? 'Editar anuncio' : 'Nuevo anuncio'">
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
            <label class="mb-1 block text-sm font-medium text-mk-text">URL de imagen (opcional)</label>
            <input
              v-model="imagenUrl"
              type="text"
              maxlength="255"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">URL de enlace (opcional)</label>
            <input
              v-model="enlaceUrl"
              type="text"
              maxlength="255"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
        </div>

        <div class="grid grid-cols-2 gap-4">
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Visible desde</label>
            <input
              v-model="fechaInicioVisible"
              type="date"
              class="mk-input w-full rounded-md border border-mk-border px-3 py-2 text-sm"
            />
          </div>
          <div>
            <label class="mb-1 block text-sm font-medium text-mk-text">Visible hasta (opcional)</label>
            <input
              v-model="fechaFinVisible"
              type="date"
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

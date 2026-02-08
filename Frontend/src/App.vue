<script setup>
import { RouterView } from 'vue-router';
</script>

<template>
  <RouterView v-slot="{ Component, route }">
    <!-- Dynamically use the layout specified in the route -->
    <component :is="route.meta.layout || 'div'">
      <component :is="Component" /> <!-- Render de / component that is Home.vue-->
    </component>
  </RouterView>
</template>

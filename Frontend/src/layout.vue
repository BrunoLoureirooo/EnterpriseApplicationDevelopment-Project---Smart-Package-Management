<template>
    <div>
      <Header /> <!-- Single inclusion of the Header -->
      <main>
        <slot></slot> <!-- Render route-specific content -->
      </main>
    </div>
  </template>
  
  <script>
  import Header from '@/components/Header.vue';
  
  export default {
    name: 'Layout',
    components: {
      Header,
    },
  };
  </script>
  
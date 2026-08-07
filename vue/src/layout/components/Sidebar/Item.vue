<template>
  <div>
    <svg-icon v-if="icon" :icon-class="icon" />
    <span v-if="title">{{ title }}</span>
  </div>
</template>

<script>
export default {
  name: 'MenuItem',
  functional: false,
  props: {
    icon: {
      type: String,
      default: ''
    },
    title: {
      type: String,
      default: ''
    }
  }
}
</script>

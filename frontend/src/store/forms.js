import { defineStore } from 'pinia';
import { ref, reactive } from 'vue';
import api from '../api';
import { useAuthStore } from './auth';
import { formApi } from '../api';

export const useFormsStore = defineStore('forms', () => {
  const forms = ref([]);
  const form = ref(null);
  const loading = ref(false);

  // Fetch my forms by status
  const fetchMyForms = async (status, userId) => {
    loading.value = true;
    try {
      const response = await api.get('/forms/my', { params: { status, userId } });
      forms.value = response.data || [];
      return forms.value;
    } catch (error) {
      console.error('Error fetching my forms:', error);
      return [];
    } finally {
      loading.value = false;
    }
  };

  // Fetch actions list (for "For My Action" page)
  const fetchActionsList = async (sortBy) => {
    loading.value = true;
    try {
      const response = await api.get('/forms/action', { params: { sortBy } });
      forms.value = response.data || [];
      return forms.value;
    } catch (error) {
      console.error('Error fetching actions list:', error);
      return [];
    } finally {
      loading.value = false;
    }
  };

  // Fetch form by ID
  const fetchFormById = async (id) => {
    loading.value = true;
    try {
      const response = await api.get(`/forms/${id}`);
      form.value = response.data;
      return form.value;
    } catch (error) {
      console.error('Error fetching form:', error);
      return null;
    } finally {
      loading.value = false;
    }
  };

  // Submit form (create new or update existing)
  const submitForm = async (id, formData) => {
    loading.value = true;
    try {
      const authStore = useAuthStore();
      const userId = authStore.user?.id;
      
      if (!id) {
        // Create new form
        const response = await formApi.createForm({ ...formData, status: 'PROCESSING' }, userId);
        return { success: true, data: response.data };
      } else {
        // Update existing form
        const response = await formApi.submitForm(id, { ...formData, status: 'PROCESSING' });
        return { success: true, data: response.data };
      }
    } catch (error) {
      console.error('Error submitting form:', error);
      return { success: false, message: error.response?.data?.message || 'Submission failed' };
    } finally {
      loading.value = false;
    }
  };

  // Save draft
  const saveDraft = async (id, formData) => {
    loading.value = true;
    try {
      const authStore = useAuthStore();
      const userId = authStore.user?.id;
      
      if (!id) {
        // Create new draft
        const response = await formApi.createDraft({ ...formData, status: 'DRAFT' }, userId);
        return { success: true, data: response.data };
      } else {
        // Update existing draft
        const response = await formApi.saveDraft(id, { ...formData, status: 'DRAFT' });
        return { success: true, data: response.data };
      }
    } catch (error) {
      console.error('Error saving draft:', error);
      return { success: false, message: error.response?.data?.message || 'Save failed' };
    } finally {
      loading.value = false;
    }
  };

  // Resubmit form
  const resubmitForm = async (id, formData) => {
    loading.value = true;
    try {
      const response = await api.put(`/forms/${id}/resubmit`, formData);
      return { success: true, data: response.data };
    } catch (error) {
      console.error('Error resubmitting form:', error);
      return { success: false, message: error.response?.data?.message || 'Resubmission failed' };
    } finally {
      loading.value = false;
    }
  };

  // Delete form
  const deleteForm = async (id) => {
    loading.value = true;
    try {
      await api.delete(`/forms/${id}`);
      return { success: true };
    } catch (error) {
      console.error('Error deleting form:', error);
      return { success: false, message: error.response?.data?.message || 'Deletion failed' };
    } finally {
      loading.value = false;
    }
  };

  // Add comment to form
  const addComment = async (formId, commentData) => {
    loading.value = true;
    try {
      const response = await api.post(`/forms/${formId}/comments`, commentData);
      return { success: true, data: response.data };
    } catch (error) {
      console.error('Error adding comment:', error);
      return { success: false, message: error.response?.data?.message || 'Failed to add comment' };
    } finally {
      loading.value = false;
    }
  };

  // Download attachment
  const downloadAttachment = async (attachmentId) => {
    try {
      const response = await api.get(`/attachments/${attachmentId}/download`, {
        responseType: 'blob'
      });
      return response;
    } catch (error) {
      console.error('Error downloading attachment:', error);
      return null;
    }
  };

  return {
    forms,
    form,
    loading,
    fetchMyForms,
    fetchActionsList,
    fetchFormById,
    submitForm,
    saveDraft,
    resubmitForm,
    deleteForm,
    addComment,
    downloadAttachment
  };
});
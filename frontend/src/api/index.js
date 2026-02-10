import axios from 'axios';

const API_BASE_URL = 'http://localhost:8081/api';

const api = axios.create({
    baseURL: API_BASE_URL,
    headers: {
        'Content-Type': 'application/json'
    }
});

// Add request interceptor for auth token
api.interceptors.request.use(
    config => {
        const token = localStorage.getItem('token');
        if (token) {
            config.headers.Authorization = `Bearer ${token}`;
        }
        return config;
    },
    error => {
        return Promise.reject(error);
    }
);

// Auth API
export const authApi = {
    login: (username, password) => api.post('/auth/login', { username, password }),
    getCurrentUser: () => api.get('/auth/me')
};

// Form API
export const formApi = {
    createForm: (form, userId) => api.post('/forms/new', form, { params: { userId } }),
    createDraft: (form, userId) => api.post('/forms/draft', form, { params: { userId } }),
    saveDraft: (id, form) => api.put(`/forms/${id}/draft`, form),
    submitForm: (id, form) => api.put(`/forms/${id}/submit`, form),
    resubmitForm: (id, form) => api.put(`/forms/${id}/resubmit`, form),
    returnForm: (id, reason) => api.put(`/forms/${id}/return`, { reason }),
    recommendForm: (id, reason) => api.put(`/forms/${id}/recommend`, { reason }),
    getFormById: (id) => api.get(`/forms/${id}`),
    getMyForms: (status, userId) => api.get('/forms/my', { params: { status, userId } }),
    getActionsList: (sortBy) => api.get('/forms/action', { params: { sortBy } }),
    deleteForm: (id) => api.delete(`/forms/${id}`)
};

// Attachment API
export const attachmentApi = {
    getAttachments: (formId) => api.get(`/forms/${formId}/attachments`),
    uploadAttachment: (formId, attachment) => api.post(`/forms/${formId}/attachments`, attachment),
    downloadAttachment: (id) => api.get(`/attachments/${id}`, { responseType: 'blob' })
};

// File Upload API
export const uploadApi = {
    uploadAttachment: (formData) => api.post('/upload/attachment', formData, {
        headers: { 'Content-Type': 'multipart/form-data' }
    }),
    downloadAttachment: (id) => api.get(`/upload/attachment/${id}`, { responseType: 'blob' }),
    getAttachment: (id) => api.get(`/upload/attachment/${id}`)
};

// Comment API
export const commentApi = {
    getComments: (formId) => api.get(`/forms/${formId}/comments`),
    addComment: (formId, comment) => api.post(`/forms/${formId}/comments`, comment)
};

export default api;
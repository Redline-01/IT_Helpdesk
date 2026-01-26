// Main JavaScript file for IT Helpdesk

// Auto-hide alerts after 5 seconds
document.addEventListener('DOMContentLoaded', function() {
    const alerts = document.querySelectorAll('.alert');
    alerts.forEach(alert => {
        setTimeout(() => {
            const bsAlert = new bootstrap.Alert(alert);
            bsAlert.close();
        }, 5000);
    });

    // Highlight active navigation link
    highlightActiveNav();
});

// Highlight active navigation link based on current URL
function highlightActiveNav() {
    const currentPath = window.location.pathname;
    const navLinks = document.querySelectorAll('.sidebar .nav-link');

    navLinks.forEach(link => {
        const href = link.getAttribute('href');
        if (href && currentPath.startsWith(href) && href !== '/') {
            link.classList.add('active');
        } else if (href === currentPath) {
            link.classList.add('active');
        }
    });
}

// ...existing code...

// Confirm delete actions
function confirmDelete(message) {
    return confirm(message || 'Are you sure you want to delete this item?');
}

// Live search functionality
let searchTimeout;
function liveSearch(inputId, endpoint, resultsId) {
    const input = document.getElementById(inputId);
    const resultsContainer = document.getElementById(resultsId);

    if (!input || !resultsContainer) return;

    input.addEventListener('input', function() {
        clearTimeout(searchTimeout);
        const keyword = this.value.trim();

        if (keyword.length < 2) {
            resultsContainer.innerHTML = '';
            return;
        }

        searchTimeout = setTimeout(() => {
            fetch(`${endpoint}?keyword=${encodeURIComponent(keyword)}`)
                .then(response => response.json())
                .then(data => {
                    displayResults(data, resultsContainer);
                })
                .catch(error => {
                    console.error('Search error:', error);
                    resultsContainer.innerHTML = '<div class="alert alert-danger">Search failed</div>';
                });
        }, 300);
    });
}

function displayResults(data, container) {
    if (!data || data.length === 0) {
        container.innerHTML = '<div class="alert alert-info">No results found</div>';
        return;
    }

    let html = '<div class="list-group">';
    data.forEach(item => {
        html += `
            <a href="/tickets/${item.id}" class="list-group-item list-group-item-action">
                <div class="d-flex w-100 justify-content-between">
                    <h6 class="mb-1">${item.title}</h6>
                    <small class="badge bg-${item.status.badgeClass}">${item.status.displayName}</small>
                </div>
                <small class="text-muted">#${item.id} - ${item.category.displayName}</small>
            </a>
        `;
    });
    html += '</div>';
    container.innerHTML = html;
}

// Toast notifications
function showToast(message, type = 'success') {
    const toastContainer = document.getElementById('toastContainer');
    if (!toastContainer) {
        const container = document.createElement('div');
        container.id = 'toastContainer';
        container.className = 'toast-container position-fixed top-0 end-0 p-3';
        document.body.appendChild(container);
    }

    const toastId = 'toast-' + Date.now();
    const toast = `
        <div id="${toastId}" class="toast" role="alert" aria-live="assertive" aria-atomic="true">
            <div class="toast-header bg-${type} text-white">
                <i class="fas fa-${type === 'success' ? 'check-circle' : 'exclamation-circle'} me-2"></i>
                <strong class="me-auto">Notification</strong>
                <button type="button" class="btn-close btn-close-white" data-bs-dismiss="toast"></button>
            </div>
            <div class="toast-body">
                ${message}
            </div>
        </div>
    `;

    document.getElementById('toastContainer').insertAdjacentHTML('beforeend', toast);
    const toastElement = document.getElementById(toastId);
    const bsToast = new bootstrap.Toast(toastElement);
    bsToast.show();

    toastElement.addEventListener('hidden.bs.toast', () => {
        toastElement.remove();
    });
}

// Form validation enhancement
function validateForm(formId) {
    const form = document.getElementById(formId);
    if (!form) return;

    form.addEventListener('submit', function(event) {
        if (!form.checkValidity()) {
            event.preventDefault();
            event.stopPropagation();
        }
        form.classList.add('was-validated');
    });
}

// Ticket status update via AJAX
function updateTicketStatus(ticketId, status) {
    fetch(`/api/tickets/${ticketId}/status?status=${status}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        showToast('Ticket status updated successfully', 'success');
        setTimeout(() => location.reload(), 1000);
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Failed to update ticket status', 'danger');
    });
}

// Assign ticket via AJAX
function assignTicket(ticketId, userId) {
    fetch(`/api/tickets/${ticketId}/assign?userId=${userId}`, {
        method: 'POST',
        headers: {
            'Content-Type': 'application/json',
        }
    })
    .then(response => response.json())
    .then(data => {
        showToast('Ticket assigned successfully', 'success');
        setTimeout(() => location.reload(), 1000);
    })
    .catch(error => {
        console.error('Error:', error);
        showToast('Failed to assign ticket', 'danger');
    });
}

// Initialize tooltips
document.addEventListener('DOMContentLoaded', function() {
    const tooltipTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="tooltip"]'));
    tooltipTriggerList.map(function (tooltipTriggerEl) {
        return new bootstrap.Tooltip(tooltipTriggerEl);
    });
});

// Initialize popovers
document.addEventListener('DOMContentLoaded', function() {
    const popoverTriggerList = [].slice.call(document.querySelectorAll('[data-bs-toggle="popover"]'));
    popoverTriggerList.map(function (popoverTriggerEl) {
        return new bootstrap.Popover(popoverTriggerEl);
    });
});

// Copy to clipboard functionality
function copyToClipboard(text) {
    navigator.clipboard.writeText(text).then(() => {
        showToast('Copied to clipboard!', 'success');
    }).catch(err => {
        console.error('Failed to copy:', err);
        showToast('Failed to copy', 'danger');
    });
}

// Print ticket
function printTicket() {
    window.print();
}

// Export functionality
function exportData(format) {
    // Implement export functionality based on format (CSV, PDF, Excel)
    showToast(`Exporting data as ${format}...`, 'info');
}

// Real-time clock
function updateClock() {
    const clockElement = document.getElementById('liveClock');
    if (clockElement) {
        const now = new Date();
        clockElement.textContent = now.toLocaleTimeString();
    }
}

setInterval(updateClock, 1000);

// Charts JavaScript for IT Helpdesk Dashboard

// Common chart options
const commonOptions = {
    responsive: true,
    maintainAspectRatio: true,
    plugins: {
        legend: {
            display: true,
            position: 'bottom',
            labels: {
                padding: 15,
                font: {
                    size: 12,
                    family: "'Segoe UI', Tahoma, Geneva, Verdana, sans-serif"
                }
            }
        },
        tooltip: {
            backgroundColor: 'rgba(0, 0, 0, 0.8)',
            padding: 12,
            cornerRadius: 8,
            titleFont: {
                size: 14
            },
            bodyFont: {
                size: 13
            }
        }
    }
};

// Color schemes
const colorSchemes = {
    status: {
        open: '#ffc107',
        inProgress: '#17a2b8',
        resolved: '#28a745',
        closed: '#6c757d',
        reopened: '#dc3545'
    },
    priority: {
        low: '#28a745',
        medium: '#ffc107',
        high: '#fd7e14',
        urgent: '#dc3545'
    },
    gradient: [
        'rgba(102, 126, 234, 0.8)',
        'rgba(118, 75, 162, 0.8)',
        'rgba(247, 37, 133, 0.8)',
        'rgba(181, 23, 158, 0.8)',
        'rgba(114, 9, 183, 0.8)'
    ]
};

// Create Doughnut Chart
function createDoughnutChart(canvasId, labels, data, colors) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;

    return new Chart(ctx, {
        type: 'doughnut',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors,
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: {
            ...commonOptions,
            cutout: '60%'
        }
    });
}

// Create Bar Chart
function createBarChart(canvasId, labels, data, label, color) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;

    return new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: [{
                label: label,
                data: data,
                backgroundColor: color || colorSchemes.gradient,
                borderRadius: 8,
                borderWidth: 0
            }]
        },
        options: {
            ...commonOptions,
            scales: {
                y: {
                    beginAtZero: true,
                    ticks: {
                        stepSize: 1
                    },
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            }
        }
    });
}

// Create Line Chart
function createLineChart(canvasId, labels, datasets) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;

    return new Chart(ctx, {
        type: 'line',
        data: {
            labels: labels,
            datasets: datasets
        },
        options: {
            ...commonOptions,
            scales: {
                y: {
                    beginAtZero: true,
                    grid: {
                        color: 'rgba(0, 0, 0, 0.05)'
                    }
                },
                x: {
                    grid: {
                        display: false
                    }
                }
            },
            elements: {
                line: {
                    tension: 0.4
                },
                point: {
                    radius: 4,
                    hitRadius: 10,
                    hoverRadius: 6
                }
            }
        }
    });
}

// Create Pie Chart
function createPieChart(canvasId, labels, data, colors) {
    const ctx = document.getElementById(canvasId);
    if (!ctx) return null;

    return new Chart(ctx, {
        type: 'pie',
        data: {
            labels: labels,
            datasets: [{
                data: data,
                backgroundColor: colors,
                borderWidth: 2,
                borderColor: '#fff'
            }]
        },
        options: commonOptions
    });
}

// Fetch and render dashboard charts
function renderDashboardCharts() {
    fetch('/api/stats')
        .then(response => response.json())
        .then(stats => {
            // Status Distribution Chart
            if (document.getElementById('statusChart') || document.getElementById('adminStatusChart')) {
                const statusLabels = ['Open', 'In Progress', 'Resolved', 'Closed'];
                const statusData = [
                    stats.openTickets,
                    stats.inProgressTickets,
                    stats.resolvedTickets,
                    stats.closedTickets
                ];
                const statusColors = [
                    colorSchemes.status.open,
                    colorSchemes.status.inProgress,
                    colorSchemes.status.resolved,
                    colorSchemes.status.closed
                ];

                createDoughnutChart('statusChart', statusLabels, statusData, statusColors);
                createDoughnutChart('adminStatusChart', statusLabels, statusData, statusColors);
            }

            // Priority Chart
            if (document.getElementById('priorityChart') || document.getElementById('adminPriorityChart')) {
                const priorityLabels = ['Urgent', 'High'];
                const priorityData = [stats.urgentTickets, stats.highPriorityTickets];
                const priorityColors = [colorSchemes.priority.urgent, colorSchemes.priority.high];

                createBarChart('priorityChart', priorityLabels, priorityData, 'Tickets', priorityColors);
                createBarChart('adminPriorityChart', priorityLabels, priorityData, 'Tickets', priorityColors);
            }
        })
        .catch(error => {
            console.error('Error fetching stats:', error);
        });
}

// Initialize charts when DOM is loaded
document.addEventListener('DOMContentLoaded', function() {
    renderDashboardCharts();
});

// Refresh charts periodically (every 5 minutes)
setInterval(function() {
    renderDashboardCharts();
}, 300000);

// Export chart as image
function exportChartAsImage(chartId, filename) {
    const canvas = document.getElementById(chartId);
    if (!canvas) return;

    const url = canvas.toDataURL('image/png');
    const link = document.createElement('a');
    link.download = filename || 'chart.png';
    link.href = url;
    link.click();
}

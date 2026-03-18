import { formatDate, formatTime as formatTimeRelative } from './index'

/**
 * Format datetime to YYYY-MM-DD HH:mm format
 * @param {string|number} datetime
 * @returns {string}
 */
export function formatDateTime(datetime) {
  if (!datetime) return ''
  return formatDate(datetime)
}

/**
 * Format time to relative time (e.g., "5分钟前")
 * @param {string|number} time
 * @returns {string}
 */
export function formatTime(time) {
  return formatTimeRelative(time)
}

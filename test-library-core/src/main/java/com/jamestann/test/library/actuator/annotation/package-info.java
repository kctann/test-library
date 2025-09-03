/*
[003][M1][監控註解系統]
監控註解包
提供精確控制監控行為的註解
*/

/**
 * Monitoring annotations for precise control of application monitoring.
 * <p>
 * This package provides annotations that allow users to explicitly control
 * which methods and classes should be monitored by the Test Library actuator system.
 * 
 * <h2>Core Annotations</h2>
 * <ul>
 * <li>{@link com.jamestann.test.library.actuator.annotation.IncludeMonitoring} - 
 *     Explicitly include a method for monitoring with custom metric name</li>
 * <li>{@link com.jamestann.test.library.actuator.annotation.ExcludeMonitoring} - 
 *     Explicitly exclude a method or class from monitoring</li>
 * </ul>
 * 
 * <h2>Usage Philosophy</h2>
 * <p>
 * These annotations follow the principle of <strong>explicit over implicit</strong> monitoring.
 * Rather than automatically monitoring all methods and trying to sanitize paths,
 * users explicitly declare what should be monitored and how.
 * 
 * <h2>Best Practices</h2>
 * <ul>
 * <li>Use meaningful business names for metrics (e.g., "user.login" not "/api/auth/login")</li>
 * <li>Provide clear descriptions for monitoring annotations</li>
 * <li>Use exclusion annotations for internal methods and health checks</li>
 * <li>Be mindful of high cardinality when using tags and parameters</li>
 * </ul>
 * 
 * <h2>Example Usage</h2>
 * <pre>{@code
 * @RestController
 * public class UserController {
 * 
 *     @GetMapping("/users/{id}")
 *     @IncludeMonitoring(
 *         name = "user.get",
 *         description = "Retrieve user by ID",
 *         tags = {"operation=read", "resource=user"}
 *     )
 *     public User getUser(@PathVariable String id) {
 *         return userService.getUser(id);
 *     }
 * 
 *     @GetMapping("/health")
 *     @ExcludeMonitoring(reason = "Internal health check")
 *     public String health() {
 *         return "OK";
 *     }
 * }
 * }</pre>
 * 
 * @author James Tann
 * @since 1.0.0
 */
/*
[003][M1][監控註解系統]
監控註解包說明
提供明確控制監控行為的註解，遵循明確優於隱式的監控原則
*/
package com.jamestann.test.library.actuator.annotation;
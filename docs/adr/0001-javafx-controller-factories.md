# ADR 0001: JavaFX Dependency Injection via Controller Factories

## Status
Approved

## Context
JavaFX controllers are reflectively instantiated by `FXMLLoader` when loading `.fxml` resource layouts. By default, this uses zero-argument constructors, which forces controllers to fetch services via static imports or global singletons. 

To introduce the `TunnelManager` and `HostCommunicator` seams, we needed to pass dependencies to controllers without binding them to static production classes, which would ruin unit testability.

We considered two options:
1. **Global Service Registry (Service Locator)**: A static class providing access to service implementations (e.g. `Services.getTunnelManager()`).
2. **Constructor Injection via `FXMLLoader.setControllerFactory`**: Setting a factory lambda on each loader instance to construct controller instances with explicit constructor parameters.

## Decision
We chose **Constructor Injection via FXMLLoader Controller Factories**. 

All controllers now define their service dependencies explicitly in their constructors. The `MainApp` class registers a resolver lambda on loaders to supply global service instances (`SystemTunnelManager`, `SystemHostCommunicator`).

## Consequences
- **Pros**:
  - Strict seam boundaries: Controllers accept interfaces rather than concrete static helpers, keeping them testable.
  - High visibility of dependencies: Constructor signatures clearly define what services the controller requires.
  - Unit tests can mock dependencies without modifying global state.
- **Cons**:
  - Requires setting a `.setControllerFactory(...)` on loaders in `MainApp.java`, introducing minor boilerplate.

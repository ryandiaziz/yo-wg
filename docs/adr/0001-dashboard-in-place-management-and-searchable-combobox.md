# 1. Dashboard In-Place Management and Searchable ComboBoxes

Date: 2026-07-25

## Status

Accepted

## Context

Users managing network tunnels, access nodes, and associated resources frequently switch contexts between the main dashboard and CRUD management sub-pages.
Specifically:
1. When searching for an Access Server under a Wireguard Tunnel on the dashboard and finding none, users had to navigate away to the Access Server menu to add it, then navigate back.
2. When configuring an Access Server or managing its web resources, users had to switch pages back and forth.
3. Dropdown choices (`ComboBox`) with many entries (e.g., Wireguard Tunnels, Credentials, Access Servers) were difficult to navigate without real-time filtering/search capabilities.

## Decision

We decided to implement:
1. **In-Place Dashboard Management**:
   - Add a "+ Add Access" button directly to the Wireguard detail header panel on the dashboard.
   - Add a Context Menu (Right-click) to Access Server card items in the dashboard to edit or delete Access Servers in-place.
   - Enhance the Resources dialog on the dashboard to allow adding, editing, and deleting Resources without leaving the dashboard view.
2. **Searchable ComboBoxes**:
   - Introduce `SearchableComboBoxUtil` to enable real-time text-based filtering on JavaFX `ComboBox` controls across form views.

## Consequences

- Improved UX: Operations can be completed directly from the dashboard view with zero page transitions.
- Better Form Usability: Form dropdowns with large datasets can be filtered instantly by typing.
- Modular Callbacks: `AccessComp` accepts update callbacks to keep the parent view seamlessly in sync without page reloads.

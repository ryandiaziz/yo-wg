# ADR 0004: Auto-Sync System WireGuard Tunnels

## Status
Accepted

## Context
When a new user installs `yo-wg`, or when a user manages WireGuard tunnels directly from the terminal (e.g. creating/modifying `/etc/wireguard/*.conf` manually), the application's local SQLite database (`wireguards` table) may not be in sync with the configuration files present in `/etc/wireguard/`.

We considered three approaches for handling external WireGuard config files:
1. **Manual Add Only**: Rely on the user manually entering or importing each config through the UI.
2. **One-Way Auto-Import (Insert Only)**: Auto-import missing `.conf` files into SQLite, but ignore subsequent changes to existing `.conf` files on disk.
3. **Hybrid Automatic Sync (Insert New + Update Modified)**: Automatically scan `/etc/wireguard/` at startup and via a manual 'Sync' UI button. Insert any newly discovered `.conf` files, and update existing records in SQLite if the file content on disk has changed.

## Decision
We chose **Hybrid Automatic Sync (Insert New + Update Modified)**.

1. **System Scanner Service (`TunnelSyncService`)**:
   - Scans `/etc/wireguard/*.conf` files.
   - Attempts direct file reading first. If permission is denied (due to root-only `/etc/wireguard` directory permissions), falls back to `sudo cat` using the saved `sudo_password` from `SettingsDAO`.
2. **Sync Logic**:
   - For every `.conf` file found in `/etc/wireguard/`:
     - If the tunnel name does not exist in the database, insert a new record with `name`, `content`, and default `note` ("Imported from /etc/wireguard").
     - If the tunnel name already exists in the database, compare the file content with the database content. If different, update the database content to reflect disk changes.
3. **Trigger**:
   - Initial automatic execution on **first application launch** (tracked via `initial_sync_completed` setting flag in database). Subsequent launches bypass auto-sync at startup.
   - Manual trigger via a **Sync** button (`btnSync`) on the WireGuard list UI (`WireguardController`).


## Consequences
- **Pros**:
  - Seamless onboarding for existing terminal users.
  - Keeps application database up-to-date with system config edits made externally.
  - Non-destructive: Does not delete database entries or remote access profiles if a file is temporarily removed or moved on disk.
- **Cons**:
  - Requires `sudo` access/password if `/etc/wireguard/` is restricted to root.

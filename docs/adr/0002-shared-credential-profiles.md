# ADR 0002: Shared Credential Profiles for SSH Automation

## Status
Proposed

## Context
Currently, when launching SSH connections for an **Access** server, the application spawns a terminal window running the native `ssh` client. The user must manually input the password in the terminal window. To improve user efficiency, we want to automate the login.

We want to allow users to store SSH passwords and private key paths. Since multiple servers often share the same credentials (e.g. the same root password or private key), storing them directly inside each individual Access node leads to configuration duplication and makes credential rotation tedious.

We considered two options:
1. **Direct Access Credentials**: Adding username, password, and private key fields directly to the `access` database table.
2. **Shared Credential Profiles**: Creating a separate `Credential` entity/table and linking Access nodes to it.

For automation, we also considered:
1. **In-App Terminal Emulator**: Implementing a full terminal component using an SSH library (e.g. JSch) in Java. This would require substantial UI work to support sizing, scrolling, keyboard input, and standard shell utilities.
2. **Native Terminal Automation**: Spawning native terminal windows with private key parameters (`ssh -i`) or password piping using `sshpass` (`sshpass -p`).

## Decision
We chose **Shared Credential Profiles** and **Native Terminal Automation**.

Each Access node can link to a shared `Credential` profile. The app will spawn the native terminal (`gnome-terminal`) and automate the connection:
- If a Private Key path is specified, it runs `ssh -i <key_path>`.
- If a Password is specified, it runs `sshpass -p '<password>' ssh`. If `sshpass` is not installed on the system, the application will display a clear warning/alert instructing the user to install it (`sudo apt install sshpass`) and fall back to prompting for the password manually in the spawned terminal.

## Consequences
- **Pros**:
  - Reusability: Credential rotation (changing a password or key path) is done in one place and applies to all linked Access nodes.
  - Native performance: Spawning the system terminal preserves local terminal preferences, keys, settings, and full features.
  - Flexibility: Supports both password-based and key-based environments.
- **Cons**:
  - Requires `sshpass` on the host system to automate password login (private keys work natively).

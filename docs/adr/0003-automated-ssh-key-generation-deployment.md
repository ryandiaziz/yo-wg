# ADR 0003: Automated SSH Key Generation and Deployment

## Status
Proposed

## Context
To offer a fully automated SSH Key setup (Otomatis Penuh), the application must generate an SSH key pair locally and deploy the public key to the remote server's `authorized_keys` file.

We considered two primary approaches for key generation and remote upload:
1. **In-App Java SSH Library (JSch/Apache SSHD)**: Generating the keys programmatically in Java and connecting via a Java-native SSH client to write the keys. This is robust but significantly increases the dependency footprint of the application and requires custom remote file writing logic.
2. **Native OS CLI Utilities**: Calling native OS utilities (`ssh-keygen` and `sshpass` command execution via `ProcessBuilder`). Since the target OS is Linux and we already rely on native utilities (`gnome-terminal`, `ssh`, `ping`), using native commands fits the existing runtime assumptions and is lightweight.

## Decision
We chose **Native OS CLI Utilities** to perform both key generation and remote key deployment.

1. **Key Generation**: We run the command:
   ```bash
   ssh-keygen -t ed25519 -f ~/.ssh/yo-wg/id_yowg_<profile_name> -N ""
   ```
   This generates a modern, secure Ed25519 key pair without a passphrase, storing it under the user's home SSH directory in a subfolder (`~/.ssh/yo-wg/`).
2. **Deployment**: We read the generated public key (`~/.ssh/yo-wg/id_yowg_<profile_name>.pub`) and run the remote command via `sshpass`:
   ```bash
   sshpass -p '<password>' ssh -p <port> -o StrictHostKeyChecking=no <user>@<address> "mkdir -p ~/.ssh && chmod 700 ~/.ssh && echo '<public_key_content>' >> ~/.ssh/authorized_keys && chmod 600 ~/.ssh/authorized_keys"
   ```
3. **Safety Fallback**: If `sshpass` is not installed, the application aborts and alerts the user to install it.

## Consequences
- **Pros**:
  - Extremely lightweight: No massive third-party Java libraries or additional jar size.
  - Native SSH Key compatibility: Generated keys are fully managed by the standard `ssh-keygen` utility, ensuring compatibility with the OS's native ssh client.
  - Robust deployment: Handles remote folder creation and sets correct permission modes (`700` and `600`) natively.
- **Cons**:
  - Depends on `sshpass` being present on the local machine for key deployment (though we check and warn beforehand).

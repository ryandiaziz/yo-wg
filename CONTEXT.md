# Glossary

- **Access**: Represents a target server or node in the system. It contains connection details such as the host address (IP or hostname), SSH credentials (`sshUser`, `sshPort`), and an associated `wireguardId`.
- **Wireguard (or Wireguard Tunnel)**: Represents a virtual private network interface configuration. It consists of a name, user notes, and configuration file settings (keys, routing, peers) used to establish network tunnels.
- **Resource**: An application URL, admin console, or web service associated with an **Access** target node, allowing direct access from the dashboard.
- **Credential Profile**: Reusable authentication configurations (username, type [password or private key path], and the secret) that can be linked to multiple **Access** nodes to automate SSH login.

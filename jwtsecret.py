import secrets
import base64

# Gera 256 bits (32 bytes) de entropia
key = secrets.token_bytes(32)

# Codifica em Base64 PADRÃO (o que o Java jjwt espera)
# Mudei de urlsafe_b64encode para standard_b64encode
secret_key = base64.standard_b64encode(key).decode()

print("Chave secreta JWT:", secret_key)
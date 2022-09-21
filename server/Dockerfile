# Start from the official Base Image
FROM python:3.9

# Copy the file with the requirements to the '/app' directory.
COPY app/requirements.txt /code/requirements.txt

# Install requirements
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt

RUN pip install fastapi uvicorn

COPY ./start.sh /start.sh

RUN chmod +x /start.sh

COPY ./app /app

CMD ["./start.sh"]

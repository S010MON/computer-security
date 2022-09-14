# Start from the official Base Image
FROM python:3.9

# Set the working directory to the '/app' directory where code will live.
WORKDIR /code

# Copy the file with the requirements to the '/app' directory.
COPY app/requirements.txt /code/requirements.txt

# Install requirements
RUN pip install --no-cache-dir --upgrade -r /code/requirements.txt

# Copy all of the code from the origin to the docker mounted directory
COPY ./app /code/app

# Run the server
CMD ["uvicorn", "app.main:app", "--host", "0.0.0.0", "--port", "80"]

Frontend API Guide

All API requests must go through the API Gateway. Do not hit the backend microservices directly.

Base URL: https://gateway-service-sc5r.onrender.com

Global Rules:

    Always send Content-Type: application/json for POST/PUT requests.

    For protected routes, include the JWT in the header: Authorization: Bearer <token>.

    Use the exact paths below (the Gateway handles internal /api/ routing automatically).

1. Login (Public)

Authenticate and get the JWT.

    POST /auth/login

    Body: ```json
    {
    "username": "student_name",
    "password": "secret_password"
    }

    Success (200 OK): Returns the user profile and the token. Save this token in localStorage.

2. Validate Session (Protected)

Check if the user's current token is still valid (use on app load or route changes).

    GET /auth/validate

    Headers: Authorization: Bearer <token>

    Success (200 OK): Returns the user's profile data.

    Error (401): Token is expired/invalid. Clear the token and redirect to login.

3. Register User (Protected - ADMIN ONLY)

Create a new user. The request must be made with an Admin's token.

    POST /auth/register

    Headers: Authorization: Bearer <admin_token>

    Body:
    JSON

    {
      "username": "new_user",
      "password": "password123",
      "fullName": "Jane Doe",
      "email": "jane@sgsits.edu",
      "role": "STUDENT", 
      "subRole": "NONE" 
    }

    Success (200 OK): User registered successfully.

Quick Example (Login):
JavaScript

const response = await fetch('https://gateway-service-sc5r.onrender.com/auth/login', {
  method: 'POST',
  headers: { 'Content-Type': 'application/json' },
  body: JSON.stringify({ username: 'test', password: 'password' })
});
const data = await response.json();
localStorage.setItem('token', data.token); // Save token for future requests

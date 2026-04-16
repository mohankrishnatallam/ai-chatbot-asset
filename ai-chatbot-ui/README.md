# AI Chatbot UI

A React-based user interface for the AI Chatbot application, built with Vite for fast development and optimized builds.

## Prerequisites

- Node.js (version 16 or higher)
- npm (comes with Node.js)

## Installation

1. Navigate to the UI project directory:
   ```
   cd ai-chatbot-ui
   ```

2. Install the dependencies:
   ```
   npm install
   ```

## Running the Application Locally

1. Start the development server:
   ```
   npm run dev
   ```

2. Open your browser and navigate to `http://localhost:5173` to view the application.

The development server supports hot module replacement, so changes to the code will be reflected immediately in the browser.

## Building for Production

To create a production build:

```
npm run build
```

This will generate optimized files in the `dist` directory, ready for deployment.

## Linting

To check code quality and run the linter:

```
npm run lint
```

## Project Structure

- `src/App.jsx` - Main application component with login page
- `src/App.css` - Styles for the login interface
- `src/main.jsx` - Application entry point
- `public/` - Static assets
- `dist/` - Production build output (after running `npm run build`)

## Technologies Used

- **React** - UI library
- **Vite** - Build tool and development server
- **ESLint** - Code linting

## Integration with API

This UI is designed to work with the AI Chatbot API located in the `ai-chatbot-api` folder. Ensure the API is running for full functionality.

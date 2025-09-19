```md
# Developer Log - Baking App

## 8 July 2025
FRONTEND:
- Scaffolded React project for Baking App frontend web UI.
- Set rough project structure with components and pages folders.

## 09 July 2025
FRONTEND:
- Created develop branch as base for feature branches.

Branch: feature/improve-all-blog-components
- Added default page structures to pages for ease of future dev.
- Added index.js to export all pages from file.
- Updated README to include project structure and component hierarchy.
- Updated BlogCard component with basic structure.

## 10 July 2025
FRONTEND:
Branch: feature/improve-all-blog-components
- Set 'Read More' in BlogCard component as a link - potentially to open blog in a new page
- FullBlog component - retrieving id using useParams from BlogsPage and embedding the id in title.
- Created a common component, modal, to hold BlogForm, created functions to set it visible/invisible.
- Created BlogsList component to hold Blogs, BlogForm and Modal components
- Added CSS classes to add styles for all Blog components.
- Updated index.css which decorates the frontend.
- Added mock data for testing.
- Updated BlogCard to set title to link which leads to FullBlog component, included content prop to display blog content.

## 30 July 2025
FRONTEND:
Branch: feature/improve-all-blog-components
- Added htmlFor attributes to labels in BlogForm.
- Added states and functions to submit form in BlogForm.
- Added functions and return components to display new blogs in BlogsList.

## 5 Aug 2025
FRONTEND:
Branch: feature/improve-all-blog-components
- Removed FullBlog route from App and moved to BlogsPage - BlogsPage will hold subroutes to all blog components.
- Added navigation routes in BlogsPage to BlogsList and FullBlog components.
- Updated FullBlog component to add logic which displays specific blog details according to blog id retrieved from URL if blog can be found, display 'Blog not found' if otherwise.

## 10 Aug 2025
BACKEND:
- Initialised Spring Boot backend project with H2 database.
- Created BlogPost entity, repository, controller.
- Added `GET /api/blogs` endpoint to fetch all blogs.
- Added `POST /api/blogs` endpoint to save new blogs.

## 12 Aug 2025
FRONTEND:
Branch: feature/display-all-blogs-on-blogspage
- Added .env to store global variables & added backend API base URL.
- Installed axios to handle API calls to backend.
- Created apiClient.js to enable one axios instance to be used for all API calls, added baseURL and timeout config.
- BlogsPage - Added functions to make GET request to backend to fetch all blogs; Added conditions to display loading and error messages when blogs are being fetched and blogs cannot be fetched respectively.

BACKEND:
- Set up CORS to allow frontend app to send requests to backend API.

## 27 Aug 2025
FRONTEND:
- Merged feature/improve-all-blog-components into develop.

Branch: feature/add-new-blog
- BlogForm - Added functions to make POST request to insert new blog data to database.

## 29 Aug 2025
FRONTEND:
- Merged feature/display-all-blogs-on-blogspage into develop.

BACKEND:
- Updated .gitignore.
- Updated README to include basic project info.

## 31 Aug 2025
FRONTEND:
- Updated for blog form modal to close when add blog is successful.
- New blog post data is displayed after successfully being added.

BACKEND:
- Updated to return blog post data that's just been saved to database as a confirmation of saving success + data can then be used immdiately by frontend.

## 06 Sept 2025
FRONTEND:
- Merged feature/add-new-blog into develop.
- Added and merged feature/delete-blog into develop, which deletes blog on clicking 'Delete' button on specific blog card.
- Added feature/edit-new-blog branch.
- Updated branch so it opens blog form modal with existing blog data for edit, and on submitting, updates blog data - need to rerender blogspage to display updated blog.

BACKEND:
- Added develop as default branch.
- Added and merged feature/delete-blog into develop.
- Added feature/edit-blog branch.

## 08 Sept 2025
FRONTEND:
- Updated edit blog feature so blogs page is rerendered to display newly updated blog data.

## 11 Sept 2025
FRONTEND:
- Bug: The frontend incorrectly displayed an error stating it was "unable to create blog" even though the POST request to the backend was successful.
- Cause: The logic relied solely on the modalIsVisible state to determine whether the modal was being used to add a new blog or edit an existing one. The state was ambiguous and not sufficient to distinguish between the two actions because in both cases, the modalIsVisible is true.
- Fix: Introduced two new state variables: isAddingNewBlog and isEditingBLog - these explicityly track the modal's purpose, allowing the UI logic to correctly handle success and error states.
- Result: Blog creation now works as expected and the modal behaviour is more predictable and maintainable.
- Merged feature/edit-blog to develop.

## 15 Sept 2025
BACKEND:
- Created add-tests branch.
- Created BlogPostServiceTest - added junit tests with mock repository using Mockito to test Service layer business logic.
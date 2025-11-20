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

## 19 Sept 2025
FRONTEND:
- Updated styling for blog for consistency and aesthetics.
- Updated 'Read More' button to make it functional when text length is more than 200.
- Added a 'Back to Blogs' link in Full Blog page.
- Merged feature/edit-blog and feature/read-more-and-button-styling branches into develop

BACKEND:
- Merged feature/add-tests to develop

## 25 Sept 2025
BACKEND:
- Added branch feature/upload-image-when-creating-blog.
- Updated createNewBlog method and added required classes to handle image upload request, saves file to uploads folder and saves image URL to DB.


## 28 Sept 2025
FRONTEND:
- Added branch feature/upload-image-when-adding-new-blog-and-display-image.
- New branch- feature/edit-blog-form-handle-uploaded-imagae-and-image-reselect
- Updated timeout for making HTTP requests to backend from 10 sec to 60 sec to allow longer request processing.
- Updated Blogs section to display blogs with image if there is one.
- Updated blog form for creating new blog to enable uploading one image and remove image, preview also available.
- Updated stylings for blog form and image upload section.

BACKEND:
- Added System.out.println to print paths images being saved to disk.
- Updated .gitignore to prevent resource folder - uploads/ - from being uploaded to online repository.


## 29 Sept 2025
FRONTEND:
- Updated blog form (edit mode) to show image as preview if exists.
- Updated editBlog function to send updated blog data to backend - needs updating as there's a bug where blog data is lost when there's no change.

BACKEND:
- Moved business logic of create blog post function from controller to service class for better separations of concern.
- Updated updateBlogPost method in BlogPostService to handle update.
- Created DTO clases: BlogPostBaseData class as parent and two children - BlogPostCreateData and BlogPostUpdateData classes for data transfer from frontend to backend.
- Using @CreationTimestamp and @UpdateTimestamp for createdAt and updatedAt at BlogPost - Hibernate creates timestamp automatically in Java.


## 30 Sept 2025
FRONTEND:
- Updated editBlog function to updating pic loaded in the below scenarios:
    Scenario 1: Blog Post to be edited has pic uploaded initially
    Change: 1. Keep same pic 2. Delete pic 3. Upload another pic
    Scenario 2: Blog Post to be edited doesn't have pic initially
    Change: 1. Keep the same (no pic) 2. Upload a new pic
    - Frontend sends the same Url to backend if same pic is kept, new file sent if pic is changed, no data on file or Url sent if end state is no pic

BACKEND:
- Updated backend to handle the above scenarios by identifying whether there's an Url or file received from frontend.


## 01 Oct 2025
BACKEND:
- Added new branch - feature/edit-blog-to-enable-image-change
- Moved createBlogPost() logic from controller to service for better separation of concerns and keeping controllers thin.
- Added DTOs: BlogPostBaseData (abstract), BlogPostCreateData, BlogPostUpdateData for data transfer and image handling.
- Updated controller methods to use @ModelAttribute for binding form data to DTOs.
- Replaced @PrePersist with Hibernate @CreationTimestamp and @UpdateTimestamp for automatic timestamps - reduce dependency on type of db.
- Updated BlogPostService to handle image scenarios (keep, replace, remove) and set updatedAt / updatedBy.
- Interface methods updated to accept DTOs instead of entity.
- Temporarily commented out BlogPostServiceTest (to update later).

TODO:
- Fix createBlogPost() title field duplication.
- Update service unit tests for new DTOs.
- Add integration tests for create/update/delete flows.

FRONTEND:
feature/edit-blog-form-handle-uploaded-imagae-and-image-reselect
- moved setting default form data logic into useState directly;
- added useEffect to setImagePreview for editing blog;
- updated editBlog to handle different scenarios of editing pic uploaded

## 03 Oct 2025
BACKEND:
- New branch: feature/global-exception-handler-and-validation.
- pom.xml - added actuator, openapi and validation starter.
- Added ErrorResponseDto to unify error response details.
- Added GlobalExceptionHnadler to handle all possible errors in the app - internal server, illegal argument, entity not found etc.
- Updated application.properties to contain file storage properties such as allowed content types, file and request size.
- ImageFileStorageService - renamed from FileStorageService; added content type control flow and throws illegalargumentexception; restricted file size max 5MB
- Added field validation for BlogPostBaseData DTO so it's validated when data comes in from client.
- BlogPostController - deleted try catch block in deleteBlogPost to keep controller thin and returning void responseentity following best practice - code 204.


## 06 Oct 2025
BACKEND:
- application.properties - added logging level and set to info
- Deleted sys.out and replaced with logs throughout the app where appropriate e.g. log.warn for invalid input or log.error for serious problems - sth that needs attention immediately.


## 07 Oct 2025
BACKEND:
- New branch: update-tests
- Updated all tests BlogPostServiceTest according to the updated BlogPostService, and grouped in subclasses according to CRUD operations.
- Updated BlogPostService getBlogPostById error message to reflect the error.


## 09 Oct 2025
BACKEND:
- feature/global-exception-handler-and-validation:
    - Added ByteUtil class to handle conversion of size string to actual byte number and format bytes to a human-readable string etc 1024 B to 1 KB.
    - In ImageFileStorageService - use the ByteUtil to handle max file size check.

- update-tests:
    - BlogPostServiceTest - added fail test for createBlogPostWithImage method.
    - Added new test class - ImageFileStorageServiceTest - to test successful and unsuccessful scenarios of image upload.
    - TODO: ImageFileStorageService - need to review type of exception thrown with content type.


## 17 Oct 2025
BACKEND:
- Added constraints tests in BlogPostRepositoryTest to ensure constraint properties are set correctly, if not, exceptions are thrown as expected


## 21 Oct 2025
BACKEND:
1. BlogPostController: separated get all blog posts and get blog posts with pagination + search into two endpoints. TODO: need to review and see if separating the two is the best option
2. IBlogPostService & BlogPostService: new method getBlogPostWithConditions takes keyword and pageable as parameters for pagination & search/filter - keyword can be null but always takes pageable as it has default values in controller - always finds Page objects from repo.
3. BlogPostRepository - added new method searchBlogPosts with custom query using JPQL to return blog posts by keyword & pageable object.


## 31 Oct 2025
FRONTEND:
- new branch: feature/pagination-search
- BlogsPage - added search bar and pagination elements: page no., page navigation buttons, sort direction dropdown, sort by dropdown and added stylings for these
- updated blog card so it's always within browser frame and error code for creating blog


## 03 Nov 2025
FRONTEND:
- updated stylings and comments for pagination and search


## 04 Nov 2025
BACKEND:
1. PageResponse: new record class to structure return type for getBlogPostsWithConditions() in service - PageResponse<BlogPost> - holds metadata for Page object and blog posts list returned.
2. BlogPostController, IBlogPostService, BlogPostService - updated return type and logic that enables it

FRONTEND:
- added library - react-icons
- updated stylings for pagination and search
- moved loading and error for fetchblog logic to only apply for BlogsList and not the whole BlogsPage - meaning pagination and search elements will always display on page regardless of whether blogslist is fetched from server


## 05 Nov 2025
BACKEND:
- updated BlogPostServiceTest to include success tests for pagination & search
- added argument captor for get all blogs tests
- merged feature/pagination-sorting-filtering into develop

FRONTEND:
- deleted commented out code
- merged feature/pagination-search into develop



## 07 Nov 2025
BACKEND:
- moved application-test.properties to sit under test/resources - following best practice
- frontend was not displaying blog post pic correctly when page blog image is updated and when page refreshes
    - fixed page refresh not displaying image error by first updating controller to return full url on get all blogs with conditions
    - fixed update blog post image error by updating updateBlogPost method logic in BlogPostService - the if condition was faulty as db stores only image key while frontend receives full imageUrl
        - logic updated to account for this
- updated README to provide a more comprehensive introduction to project including features, an updated tech stack, design decisions etc
    - added architecture diagram of project generated using VxPlain - mermaid code retrieved from it
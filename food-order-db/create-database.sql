CREATE LOGIN FoodOrder WITH PASSWORD = '$(FOOD_ORDER_PASSWORD)';
EXEC sp_addsrvrolemember @loginame= 'FoodOrder', @rolename = 'dbcreator';
GO
EXECUTE AS LOGIN = 'FoodOrder'; 
CREATE DATABASE FoodOrderDb;
ALTER DATABASE FoodOrderDb SET AUTO_CLOSE OFF;
GO
USE FoodOrderDb
CREATE USER FoodOrder for LOGIN FoodOrder
ALTER ROLE db_owner ADD MEMBER FoodOrder
GO